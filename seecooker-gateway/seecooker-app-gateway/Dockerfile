FROM openjdk:17-jdk-alpine

LABEL authors="rashawn"

ARG port=9000

WORKDIR /app

COPY target/seecooker-app-gateway-0.2.0.jar /app

ENTRYPOINT ["java", "-jar", "seecooker-app-gateway-0.2.0.jar", "--server.port=${port}", "--spring.profiles.active=prod"]