FROM openjdk:latest

LABEL authors="rashawn"

ARG port=9000

WORKDIR /app

COPY seecooker-app-gateway-0.2.0.jar /app

ENTRYPOINT ["java", "-jar", "seecooker-app-gateway-0.2.0.jar", "--server.port=${port}", "--spring.profiles.active=prod"]