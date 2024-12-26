FROM  openjdk:21
LABEL maintainer="Jakex01"
WORKDIR /app
COPY ./target/order-0.0.1-SNAPSHOT.jar order-0.0.1-SNAPSHOT.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","/app/order-0.0.1-SNAPSHOT.jar"]