# Use the official OpenJDK 23 image as a base image
FROM openjdk:23-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the project files to the container
COPY build/libs/nestwise.jar ./

# Expose the port the application runs on
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "nestwise.jar"]
