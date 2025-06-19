#
# Build stage
#
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

# Set working directory inside the container
WORKDIR /app

# Copy pom.xml and download dependencies (use cache efficiently)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

#
# Runtime stage
#
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/mockapi-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java","-jar","app.jar"]
