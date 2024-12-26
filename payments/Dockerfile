FROM openjdk:21
LABEL maintainer="Jakex01"
WORKDIR /app
COPY ./target/payments-0.0.1-SNAPSHOT.jar payments-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/app/payments-0.0.1-SNAPSHOT.jar"]
EXPOSE 8081