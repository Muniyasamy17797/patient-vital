# vital.dockerfile
FROM base:latest

WORKDIR /app/vital


# Copy the JAR file to the container
COPY target/vital.jar /app/vital/vital.jar

# Execute the application
CMD ["java", "-jar", "/app/vital/vital.jar"]