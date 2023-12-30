FROM openjdk:17-jdk-alpine

LABEL authors="rashawn"

ARG port=9500

WORKDIR /app

COPY seecooker-user-service-0.2.0.jar /app

ENTRYPOINT ["java", "-jar", "seecooker-user-service-0.2.0.jar", "--server.port=${port}", "--spring.profiles.active=prod"]