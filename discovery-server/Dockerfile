# Use a smaller base image for Java 17
FROM openjdk:21

# Add metadata about the image
LABEL maintainer="Jakex01" \
      description="Docker image for Eureka Discovery Server"

# Set a working directory (optional, for better organization)
WORKDIR /app

# Copy the JAR file into the container
COPY ./target/discovery-server-0.0.1-SNAPSHOT.jar discovery-server-0.0.1-SNAPSHOT.jar

# Expose the port used by Eureka Server
EXPOSE 8761

# Run the application
ENTRYPOINT ["java", "-jar", "/app/discovery-server-0.0.1-SNAPSHOT.jar"]
