FROM openjdk:21
LABEL maintainer="Jakex01"
WORKDIR /app
COPY ./target/notification-0.0.1-SNAPSHOT.jar notification-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/app/notification-0.0.1-SNAPSHOT.jar"]