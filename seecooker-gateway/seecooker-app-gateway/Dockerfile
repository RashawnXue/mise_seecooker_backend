FROM openjdk:17-jdk-alpine

LABEL authors="rashawn"

ENV PORT=9000
ENV TZ="Asia/Shanghai"

WORKDIR /app

COPY seecooker-app-gateway-0.2.0.jar /app

ENTRYPOINT ["java", "-jar", "seecooker-app-gateway-0.2.0.jar", "--server.port=${PORT}", "--spring.profiles.active=prod", "-Duser.timezone=GMT+08"]