# Order Management System

A microservices-based order management system built with Spring Boot, Kafka, and MySQL, featuring order processing, payment handling, and notification services.

## 🏗️ Architecture

The system consists of three main microservices:

- **Order Service** (Port: 8087) - Handles order creation, management, and processing
- **Payment Service** (Port: 8082) - Manages payment processing and validation
- **Notification Service** (Port: 8083) - Sends notifications for order and payment events

### Technology Stack

- **Backend**: Spring Boot 3.x, Spring Kafka, Spring Data JPA
- **Database**: MySQL 8.0
- **Message Broker**: Apache Kafka with Zookeeper
- **Containerization**: Docker & Docker Compose
- **API Documentation**: Swagger/OpenAPI 3
- **Security**: JWT Authentication

## 🚀 Quick Start

### Prerequisites

- **Docker Desktop** installed and running
- Java 21 (LTS) installed and JAVA_HOME set
- Maven 3.9+ installed
- At least 8GB RAM available

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd Order_Management
```

### 2. Start All Services

**Option 1: Use the startup script (Recommended)**
```bash
# Windows
start-services.bat

# PowerShell
.\start-services.ps1
```

**Option 2: Manual startup**
```bash
# Start infrastructure first
docker-compose up -d zookeeper kafka mysql kafka-ui

# Wait for infrastructure to be ready, then start services
docker-compose up -d order-service payment-service notification-service
```

### 3. Verify Services

Check if all services are running:

```bash
docker-compose ps
```

You should see:
- ✅ zookeeper
- ✅ kafka
- ✅ kafka-ui
- ✅ mysql
- ✅ order-service
- ✅ payment-service
- ✅ notification-service

## 📊 Service Endpoints

### Order Service (Port: 8087)
- **Swagger UI**: http://localhost:8087/swagger-ui.html
- **API Docs**: http://localhost:8087/v3/api-docs

### Payment Service (Port: 8082)
- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **API Docs**: http://localhost:8082/v3/api-docs

### Notification Service (Port: 8083)
- **Swagger UI**: http://localhost:8083/swagger-ui.html
- **API Docs**: http://localhost:8083/v3/api-docs

### Kafka UI (Port: 8081)
- **Kafka Management**: http://localhost:8081

## 🔧 Configuration

### Environment Variables

#### Order Service
```yaml
SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/order_db
SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
JWT_SECRET: your-secret-key
JWT_EXPIRATION: 3600
```

#### Payment Service
```yaml
SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/payment_db
SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
```

#### Notification Service
```yaml
SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/notification_db
SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
```

### Database Configuration

- **Host**: mysql (Docker network) / localhost (local development)
- **Port**: 3306
- **Root Password**: 4186
- **Databases**: order_db, payment_db, notification_db

### Kafka Configuration

- **Bootstrap Servers**: kafka:9092 (Docker) / localhost:29092 (local)
- **Zookeeper**: zookeeper:2181 (Docker) / localhost:2181 (local)
- **Topics**: Auto-created based on service needs

## 🧪 Testing

### 1. Health Checks

```bash
# Check service health
curl http://localhost:8087/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

### 2. API Testing

#### Create an Order
```bash
curl -X POST http://localhost:8087/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "123",
    "items": [
      {
        "productId": "456",
        "quantity": 2,
        "price": 29.99
      }
    ]
  }'
```

#### Process Payment
```bash
curl -X POST http://localhost:8082/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "1",
    "amount": 59.98,
    "paymentMethod": "CREDIT_CARD"
  }'
```

### 3. Kafka Topic Monitoring

1. Open Kafka UI: http://localhost:8081
2. Navigate to Topics
3. Monitor message flow between services

### 4. Health Check Script

Use the provided health check script to verify all services:
```bash
# Windows
health-check.bat

# Or manually check each service
curl http://localhost:8087/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

## 🔍 Troubleshooting

### Critical Issues Fixed

#### 1. Docker Desktop Not Running
**Problem**: `error during connect: Get "http://%2F%2F.%2Fpipe%2FdockerDesktopLinuxEngine/v1.51/containers/json"`
**Solution**: Start Docker Desktop and wait for it to be fully ready

#### 2. Database Dialect Issues
**Problem**: Deprecated `MySQL8Dialect` causing compatibility issues
**Solution**: Updated to modern `org.hibernate.dialect.MySQLDialect`

#### 3. Port Conflicts
**Problem**: Kafka UI was using port 8080 (common web port)
**Solution**: Changed Kafka UI to port 8081

#### 4. Spring Boot Version Mismatch
**Problem**: Root pom.xml had different Spring Boot version than services
**Solution**: Aligned all services to Spring Boot 3.5.5

### Common Issues

#### 5. Kafka Connection Errors
```
Connection to node -1 (localhost:9092) could not be established
```

**Solution**: Ensure Kafka is running and accessible
```bash
docker-compose logs kafka
docker-compose restart kafka
```

#### 6. Database Connection Issues
```
Communications link failure
```

**Solution**: Check MySQL service status
```bash
docker-compose logs mysql
docker-compose restart mysql
```

#### 7. Service Startup Failures
```
Service failed to start
```

**Solution**: Check service logs
```bash
docker-compose logs order-service
docker-compose logs payment-service
docker-compose logs notification-service
```

### Debug Commands

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f order-service

# Check service status
docker-compose ps

# Restart specific service
docker-compose restart order-service

# Rebuild and restart
docker-compose down
docker-compose up --build
```

## 🏗️ Development

### Local Development Setup

1. **Start Infrastructure Only**
   ```bash
   docker-compose up zookeeper kafka mysql kafka-ui
   ```

2. **Run Services Locally**
   ```bash
   # Order Service
   cd order-service
   mvn spring-boot:run
   
   # Payment Service
   cd ../payment-service
   mvn spring-boot:run
   
   # Notification Service
   cd ../notification-service
   mvn spring-boot:run
   ```

3. **Update application.properties for local development**
   ```properties
   # Use localhost for local development
   spring.datasource.url=jdbc:mysql://localhost:3306/order_db
   spring.kafka.bootstrap-servers=localhost:29092
   ```

### Project Structure

```
Order_Management/
├── common/                    # Shared DTOs and constants
├── order-service/            # Order management service
├── payment-service/          # Payment processing service
├── notification-service/     # Notification service
├── docker-compose.yaml      # Service orchestration
└── README.md               # This file
```

## 📝 API Documentation

Each service provides Swagger UI for interactive API documentation:

- **Order Service**: http://localhost:8087/swagger-ui.html
- **Payment Service**: http://localhost:8082/swagger-ui.html
- **Notification Service**: http://localhost:8083/swagger-ui.html

## 🚀 Deployment

### Production Considerations

1. **Environment Variables**: Use proper secrets management
2. **Database**: Use production-grade MySQL with proper backup
3. **Kafka**: Configure replication and monitoring
4. **Security**: Enable HTTPS, proper JWT secrets
5. **Monitoring**: Add health checks and metrics

### Scaling

```bash
# Scale specific services
docker-compose up --scale order-service=3
docker-compose up --scale payment-service=2
```

