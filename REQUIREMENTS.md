# Order Management System - Requirements & Dependencies

## Available JAR Files

### 1. Common Library
- **File**: `common/target/common-0.0.1-SNAPSHOT.jar`
- **Size**: 8.7KB
- **Purpose**: Shared DTOs, constants, and common utilities
- **Dependencies**: 
  - Spring Boot Starter
  - Jackson for JSON processing

### 2. Order Service
- **File**: `order-service/target/order-service.jar`
- **Size**: 86MB
- **Purpose**: Core order management functionality
- **Dependencies**:
  - Spring Boot Starter Web
  - Spring Boot Starter Data JPA
  - Spring Boot Starter Security
  - Spring Boot Starter Kafka
  - JWT for authentication
  - Common library JAR

### 3. Payment Service
- **File**: `payment-service/target/payment-service.jar`
- **Size**: 82MB
- **Purpose**: Payment processing and management
- **Dependencies**:
  - Spring Boot Starter Web
  - Spring Boot Starter Data JPA
  - Spring Boot Starter Kafka
  - Common library JAR

### 4. Notification Service
- **File**: `notification-service/target/notification-service.jar`
- **Size**: 77MB
- **Purpose**: Notification handling and delivery
- **Dependencies**:
  - Spring Boot Starter Web
  - Spring Boot Starter Data JPA
  - Spring Boot Starter Kafka
  - Common library JAR

## System Requirements

### Runtime Environment
- **Java Version**: 17 or higher
- **Spring Boot Version**: 3.x
- **Maven Version**: 3.6 or higher

### Infrastructure Requirements
- **Kafka**: For event-driven communication between services
- **Database**: PostgreSQL/MySQL for data persistence
- **Memory**: Minimum 2GB RAM per service
- **Storage**: Minimum 1GB disk space per service

### Network Requirements
- **Ports**:
  - Order Service: 8081
  - Payment Service: 8082
  - Notification Service: 8083
- **Kafka**: 9092

## Build Requirements

### Prerequisites
1. Install Java 17+
2. Install Maven 3.6+
3. Install Docker and Docker Compose

### Build Commands
```bash
# Build all services
mvn clean install -DskipTests

# Build individual services
cd common && mvn clean install
cd order-service && mvn clean install
cd payment-service && mvn clean install
cd notification-service && mvn clean install
```

### JAR File Locations
After building, JAR files will be available in:
- `common/target/common-0.0.1-SNAPSHOT.jar`
- `order-service/target/order-service.jar`
- `payment-service/target/payment-service.jar`
- `notification-service/target/notification-service.jar`

## Deployment Requirements

### Docker Deployment
- Use provided `docker-compose.yaml` for local development
- Ensure all required ports are available
- Kafka and database services must be running before starting application services

### Production Deployment
- Deploy JAR files to application servers
- Configure external Kafka cluster
- Configure external database
- Set appropriate JVM memory settings
- Configure logging and monitoring

## Dependencies Order
1. **Common Library** must be built first
2. **Order Service** depends on Common Library
3. **Payment Service** depends on Common Library
4. **Notification Service** depends on Common Library

## Health Checks
- Use provided `health-check.bat` script to verify service health
- Each service exposes health endpoints at `/actuator/health`
- Monitor Kafka connectivity and database connections

## Troubleshooting
- Check service logs for dependency issues
- Verify JAR files are properly built and accessible
- Ensure all required environment variables are set
- Verify network connectivity between services
