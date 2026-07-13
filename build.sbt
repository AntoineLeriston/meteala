scalaVersion := "3.3.1"
name := "meteo-de-dani"
version := "0.1.0-SNAPSHOT"

val sttpVersion = "3.9.0"
val circeVersion = "0.14.6"
val http4sVersion = "0.23.23"

libraryDependencies ++= Seq(
  "com.softwaremill.sttp.client3" %% "core" % sttpVersion,
  "com.softwaremill.sttp.client3" %% "circe" % sttpVersion,
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.http4s" %% "http4s-ember-server" % http4sVersion,
  "org.http4s" %% "http4s-dsl"          % http4sVersion
)
