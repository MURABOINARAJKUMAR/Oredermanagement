FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the entire project structure
COPY . .

# Install common module to local repository first
RUN cd common && mvn clean install -DskipTests

# Build payment service
RUN cd payment-service && mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/payment-service/target/payment-service.jar payment-service.jar
ENTRYPOINT ["java", "-jar", "payment-service.jar"]