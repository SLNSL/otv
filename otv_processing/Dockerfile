FROM openjdk:17-jdk-slim
WORKDIR /app

COPY target/otv_processing-0.0.1-SNAPSHOT.jar /app/processing.jar
EXPOSE 8080
ENTRYPOINT java -jar processing.jar