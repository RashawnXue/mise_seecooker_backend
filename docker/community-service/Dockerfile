FROM openjdk:17-jdk-alpine

LABEL authors="rashawn"

ARG port=9520

WORKDIR /app

COPY ./../../seecooker-service/seecooker-community-service/target/seecooker-community-service-0.2.0.jar /app

ENTRYPOINT ["java", "-jar", "seecooker-community-service-0.2.0.jar", "--server.port=${port}", "--spring.profiles.active=prod"]