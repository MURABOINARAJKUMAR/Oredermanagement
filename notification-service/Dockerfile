FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the entire project structure
COPY . .

# Install common module to local repository first
RUN cd common && mvn clean install -DskipTests

# Build notification service
RUN cd notification-service && mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/notification-service/target/notification-service.jar notification-service.jar
ENTRYPOINT ["java", "-jar", "notification-service.jar"]