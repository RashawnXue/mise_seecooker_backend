FROM openjdk:17-jdk-alpine

LABEL authors="rashawn"

ENV PORT=9500

WORKDIR /app

COPY seecooker-user-service-0.2.0.jar /app

ENTRYPOINT ["java", "-jar", "seecooker-user-service-0.2.0.jar", "--server.port=${PORT}", "--spring.profiles.active=prod"]