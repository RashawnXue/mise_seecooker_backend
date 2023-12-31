FROM openjdk:17-jdk-alpine

LABEL authors="rashawn"

ENV PORT=9520

WORKDIR /app

COPY seecooker-community-service-0.2.0.jar /app

ENTRYPOINT ["java", "-jar", "seecooker-community-service-0.2.0.jar", "--server.port=${PORT}", "--spring.profiles.active=prod"]