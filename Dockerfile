FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

COPY target/your-app.jar app.jar

# Expose the port your Spring Boot app runs on (default is 8080)
EXPOSE 8082

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
