# Use a base image with Maven and OpenJDK 17
FROM maven:3.8.4-openjdk-17 AS builder
LABEL authors="Roman Cech"

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven configuration files
COPY pom.xml /app/

# Resolve dependencies (this step will be cached if there are no changes to pom.xml)
RUN mvn -B dependency:go-offline

# Copy the source code
COPY src /app/src

# Build the application
RUN mvn -B clean package

# Use a smaller base image for the runtime environment
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=builder /app/target/*.jar /app/app.jar

# Copy the application configuration files
COPY src/main/resources/application.yaml /app/application.yaml

# Expose the port that your application runs on
EXPOSE 8080

# Command to run the application when the container starts
CMD ["java", "-jar", "app.jar"]