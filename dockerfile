FROM eclipse-temurin:17-jdk-alpine

RUN apk add --no-cache bash curl \
    && curl -fL https://github.com/sbt/sbt/releases/download/v1.9.9/sbt-1.9.9.tgz | tar xz -C /usr/local \
    && ln -s /usr/local/sbt/bin/sbt /usr/local/bin/sbt

WORKDIR /app

COPY . .

RUN sbt compile

EXPOSE 8080

CMD ["sbt", "run"]
