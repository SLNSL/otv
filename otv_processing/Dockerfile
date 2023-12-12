FROM openjdk:17-jdk-slim
WORKDIR /app

RUN apt-get update && \
    apt-get install wget postgresql-client --yes && \
    mkdir --parents ~/.postgresql && \
    wget "https://storage.yandexcloud.net/cloud-certs/CA.pem" \
         --output-document ~/.postgresql/root.crt && \
    chmod 0600 ~/.postgresql/root.crt

COPY target/otv_processing-0.0.1-SNAPSHOT.jar /app/processing.jar
EXPOSE 8080
ENTRYPOINT java -jar processing.jar