import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.ember.server._
import com.comcast.ip4s._

import sttp.client3._
import io.circe.parser._
import io.circe.generic.auto._
import java.time.{LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

case class HourlyData(time: List[String], temperature_2m: List[Option[Double]], weather_code: List[Option[Int]])
case class DailyData(time: List[String], temperature_2m_max: List[Option[Double]], temperature_2m_min: List[Option[Double]])
case class WeatherResponse(hourly: HourlyData, daily: DailyData)

object Main extends IOApp.Simple {

  object LatMatcher extends QueryParamDecoderMatcher[Double]("lat")
  object LonMatcher extends QueryParamDecoderMatcher[Double]("lon")

  def interpretWeatherCode(code: Int): String = code match {
    case 0 => "Ensoleillé / Dégagé"
    case 1 | 2 | 3 => "Nuageux"
    case 45 | 48 => "Brouillard"
    case 51 | 53 | 55 => "Bruine"
    case 61 | 63 | 65 | 80 | 81 | 82 => "Pluie"
    case 71 | 73 | 75 | 85 | 86 => "Neige"
    case 95 | 96 | 99 => "Orage"
    case _ => "Inconnu"
  }

  // NOUVEAU : Le générateur de fond d'écran dynamique
  def getBackgroundForWeather(code: Int): String = code match {
    case 0 => "linear-gradient(135deg, #FF9933, #E14E22)" // Soleil chaud
    case 1 | 2 | 3 => "linear-gradient(135deg, #8E9EAB, #EEF2F3)" // Nuages gris/bleu clair
    case 45 | 48 => "linear-gradient(135deg, #757F9A, #D7DDE8)" // Brouillard
    case 51 | 53 | 55 | 61 | 63 | 65 | 80 | 81 | 82 => "linear-gradient(135deg, #1CB5E0, #000046)" // Pluie sombre
    case 71 | 73 | 75 | 85 | 86 => "linear-gradient(135deg, #E6DADA, #274046)" // Neige froide
    case 95 | 96 | 99 => "linear-gradient(135deg, #0F2027, #203A43, #2C5364)" // Orage très sombre
    case _ => "linear-gradient(135deg, #1c1c1e, #2c2c2e)"
  }

  def getAdvancedWeather(lat: Double, lon: Double): String = {
    try {
      val backend = HttpClientSyncBackend()
      val url = uri"https://api.open-meteo.com/v1/meteofrance?latitude=$lat&longitude=$lon&hourly=temperature_2m,weather_code&daily=temperature_2m_max,temperature_2m_min&timezone=Europe%2FParis"
      
      val response = basicRequest.get(url).send(backend)
      
      response.body match {
        case Right(jsonString) =>
          decode[WeatherResponse](jsonString) match {
            case Right(weather) =>
              val now = LocalDateTime.now(ZoneId.of("Europe/Paris")).truncatedTo(ChronoUnit.HOURS)
              val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
              val currentHourStr = now.format(formatter)
              
              val index = weather.hourly.time.indexOf(currentHourStr)
              
              if (index != -1 && index + 1 < weather.hourly.temperature_2m.length) {
                val tempNow = weather.hourly.temperature_2m(index).getOrElse(0.0)
                val tempNextHour = weather.hourly.temperature_2m(index + 1).getOrElse(0.0)
                val weatherCode = weather.hourly.weather_code(index).getOrElse(-1)
                
                val condition = interpretWeatherCode(weatherCode)
                val bgColor = getBackgroundForWeather(weatherCode) // Extraction de la couleur
                
                val tempMax = weather.daily.temperature_2m_max.headOption.flatten.getOrElse(0.0)
                val tempMin = weather.daily.temperature_2m_min.headOption.flatten.getOrElse(0.0)
                
                s"""{
                   |  "actuelle": $tempNow,
                   |  "dans_1h": $tempNextHour,
                   |  "max": $tempMax,
                   |  "min": $tempMin,
                   |  "condition": "$condition",
                   |  "couleur": "$bgColor"
                   |}""".stripMargin
              } else {
                """{"erreur": "Heure non trouvée"}"""
              }
            case Left(error) => """{"erreur": "Parsing météo échoué"}"""
          }
        case Left(_) => """{"erreur": "Requête API échouée"}"""
      }
    } catch {
      case e: Exception => """{"erreur": "Serveur injoignable"}"""
    }
  }

  val meteoRoutes = HttpRoutes.of[IO] {
    case GET -> Root / "meteo" :? LatMatcher(lat) +& LonMatcher(lon) =>
      val jsonResponse = getAdvancedWeather(lat, lon)
      Ok(jsonResponse).map(_.withContentType(headers.`Content-Type`(MediaType.application.json)))
  }.orNotFound

  def run: IO[Unit] = {
    EmberServerBuilder.default[IO].withHost(ipv4"0.0.0.0").withPort(port"8080").withHttpApp(meteoRoutes).build.use(_ => IO.never)
  }
}
