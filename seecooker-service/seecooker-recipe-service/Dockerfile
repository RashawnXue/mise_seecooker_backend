FROM openjdk:17-jdk-alpine

LABEL authors="rashawn"

ENV PORT=9510
ENV TZ="Asia/Shanghai"

WORKDIR /app

COPY seecooker-recipe-service-0.2.0.jar /app

ENTRYPOINT ["java", "-jar", "seecooker-recipe-service-0.2.0.jar", "--server.port=${PORT}", "--spring.profiles.active=prod", "-Duser.timezone=GMT+08"]