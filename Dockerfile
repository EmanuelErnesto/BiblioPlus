FROM ubuntu:latest AS build

RUN apt-get update && apt-get install openjdk-17-jdk  -y

COPY . .

RUN apt-get install maven -y
RUN mvn clean install

FROM openjdk:17-jdk-slim

EXPOSE 8082

COPY --from=build /target/BiblioPlus-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
