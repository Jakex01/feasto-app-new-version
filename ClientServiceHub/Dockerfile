FROM openjdk:21
LABEL maintainer="Jakex01" \
      description="Docker image for Chat Service"
WORKDIR /app
COPY ./target/ClientServiceHub-0.0.1-SNAPSHOT.jar ClientServiceHub-0.0.1-SNAPSHOT.jar
EXPOSE 8086
ENTRYPOINT ["java","-jar","/app/ClientServiceHub-0.0.1-SNAPSHOT.jar"]

