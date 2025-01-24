FROM maven:3.9.9-eclipse-temurin-17 AS builder

RUN apt-get update && apt-get install -y docker.io

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN service docker start && \
    mvn clean package

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8082
