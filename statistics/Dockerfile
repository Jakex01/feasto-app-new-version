FROM  openjdk:21
LABEL maintainer="Jakex01"
WORKDIR /app
COPY ./target/statistics-0.0.1-SNAPSHOT.jar statistics-0.0.1-SNAPSHOT.jar
EXPOSE 8087
ENTRYPOINT ["java","-jar","/app/statistics-0.0.1-SNAPSHOT.jar"]