FROM openjdk:17-jdk-slim
WORKDIR /app

COPY target/otv_notification-0.0.1-SNAPSHOT.jar /app/notification.jar
EXPOSE 8080
ENTRYPOINT java -jar notification.jar