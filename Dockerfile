FROM ubuntu:latest AS build

RUN apt-get update && apt-get install openjdk-17-jdk  -y

WORKDIR /app

COPY . /app

#RUN echo "DB_URL: $DB_URL" && echo "DB_POSTGRES_USER: $DB_POSTGRES_USER"

RUN cp .env.pre-prod .env

RUN apt-get install maven -y
RUN mvn clean install

FROM openjdk:17-jdk-slim

EXPOSE 8082

COPY --from=build /target/BiblioPlus-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]