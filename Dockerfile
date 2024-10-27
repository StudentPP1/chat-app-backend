FROM maven:3.9.8-eclipse-temurin-21 AS builder
COPY src /app/src
COPY pom.xml /app
RUN mvn clean package -DskipTests

FROM openjdk:21
COPY --from=builder app/target/chat-app-0.0.1-SNAPSHOT.jar /app-service/app.jar
WORKDIR /app-service

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]