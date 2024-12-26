FROM openjdk:21
LABEL maintainer="Jakex01" \
      description="Docker image for Security Service"
WORKDIR /app
COPY ./target/security-service-1.0.0.jar security-service-1.0.0.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app/security-service-1.0.0.jar"]
