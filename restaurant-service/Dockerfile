FROM openjdk:21
LABEL maintainer="Jakex01" \
      description="Docker image for Restaurant Service"
WORKDIR /app
COPY ./target/restaurant-service-0.0.1-SNAPSHOT.jar restaurant-service-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/restaurant-service-0.0.1-SNAPSHOT.jar"]

