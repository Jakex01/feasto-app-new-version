FROM openjdk:21
LABEL maintainer="Jakex01"
LABEL description="Docker image for the Basket service"
WORKDIR /app
COPY ./target/basket-1.0.0.jar basket-1.0.0.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/app/basket-1.0.0.jar"]
