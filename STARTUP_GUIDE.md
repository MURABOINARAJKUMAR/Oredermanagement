# Order Management System - Startup Guide

## Prerequisites
- Java 21 (LTS) installed and JAVA_HOME set
- Maven 3.9+ installed
- Docker Desktop installed and running
- At least 8GB RAM available

## Critical Issues Fixed

### 1. Docker Desktop Not Running
**Problem**: Docker Desktop must be running before starting services
**Solution**: Start Docker Desktop and wait for it to be fully ready

### 2. Database Dialect Issues
**Problem**: Deprecated MySQL8Dialect was causing compatibility issues
**Solution**: Updated to modern `org.hibernate.dialect.MySQLDialect`

### 3. Port Conflicts
**Problem**: Kafka UI was using port 8080 (common web port)
**Solution**: Changed Kafka UI to port 8081

### 4. Spring Boot Version Mismatch
**Problem**: Root pom.xml had different Spring Boot version than services
**Solution**: Aligned all services to Spring Boot 3.5.5

## Step-by-Step Startup Process

### Step 1: Start Docker Desktop
1. Open Docker Desktop
2. Wait for Docker Engine to start (green status)
3. Ensure Docker is running: `docker ps`

### Step 2: Build the Project
```bash
mvn clean compile
```

### Step 3: Start Infrastructure Services
```bash
docker-compose up -d zookeeper kafka mysql kafka-ui
```

**Wait for all services to be healthy:**
- Zookeeper: Port 2181
- Kafka: Ports 9092, 29092
- MySQL: Port 3306
- Kafka UI: Port 8081

### Step 4: Verify Infrastructure
```bash
# Check container status
docker ps

# Check Kafka topics
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092

# Check MySQL connection
docker exec mysql mysql -u root -p4186 -e "SHOW DATABASES;"
```

### Step 5: Start Microservices
```bash
# Start Order Service
docker-compose up -d order-service

# Start Payment Service  
docker-compose up -d payment-service

# Start Notification Service
docker-compose up -d notification-service
```

### Step 6: Verify All Services
```bash
# Check all containers
docker ps

# Check service logs
docker logs order-service
docker logs payment-service
docker logs notification-service
```

## Service URLs

- **Order Service**: http://localhost:8087
- **Payment Service**: http://localhost:8082
- **Notification Service**: http://localhost:8083
- **Kafka UI**: http://localhost:8081
- **Swagger UI**: 
  - Order Service: http://localhost:8087/swagger-ui.html
  - Payment Service: http://localhost:8082/swagger-ui.html
  - Notification Service: http://localhost:8083/swagger-ui.html

## Troubleshooting

### Common Issues:

1. **Port Already in Use**
   ```bash
   # Find process using port
   netstat -ano | findstr :8087
   # Kill process
   taskkill /PID <PID> /F
   ```

2. **Database Connection Failed**
   - Ensure MySQL container is running: `docker ps`
   - Check MySQL logs: `docker logs mysql`
   - Verify credentials in application.properties

3. **Kafka Connection Failed**
   - Ensure Zookeeper and Kafka are running: `docker ps`
   - Check Kafka logs: `docker logs kafka`
   - Wait for health checks to pass

4. **Service Won't Start**
   - Check service logs: `docker logs <service-name>`
   - Verify all dependencies are healthy
   - Check application.properties configuration

### Health Check Commands:
```bash
# Check service health
curl http://localhost:8087/actuator/health
curl http://localhost:8082/actuator/health  
curl http://localhost:8083/actuator/health

# Check Kafka topics
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092

# Check MySQL databases
docker exec mysql mysql -u root -p4186 -e "SHOW DATABASES;"
```

## Development Mode

For development without Docker:
1. Start MySQL locally on port 3306
2. Start Kafka locally on port 9092
3. Update application.properties to use `localhost` instead of container names
4. Run services individually with `mvn spring-boot:run`

## Performance Tips

- Allocate at least 4GB RAM to Docker Desktop
- Use SSD storage for better I/O performance
- Monitor resource usage with `docker stats`
- Consider using Docker volumes for persistent data

## Security Notes

- Default MySQL password is `4186` - change in production
- JWT secret is hardcoded - use environment variables in production
- Kafka is running without authentication - add security in production
- All services expose internal APIs - add proper authentication/authorization
