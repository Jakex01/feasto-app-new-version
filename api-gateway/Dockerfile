FROM openjdk:21
LABEL maintainer="Jakex01"
LABEL description="Docker image for the API Gateway service"
WORKDIR /app
COPY ./target/api-gateway-0.0.1-SNAPSHOT.jar api-gateway-0.0.1-SNAPSHOT.jar
EXPOSE 8762
ENTRYPOINT ["java", "-jar", "/app/api-gateway-0.0.1-SNAPSHOT.jar"]
