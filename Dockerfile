# 选择镜像
FROM openjdk:17
WORKDIR /home/jar
COPY . /home/jar
EXPOSE 8080/tcp

CMD ["java", "-jar", "seecooker-web-test-0.0.1.jar"]
