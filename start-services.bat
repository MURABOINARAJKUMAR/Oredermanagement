@echo off
echo ========================================
echo Order Management System Startup Script
echo ========================================
echo.

echo Checking Docker status...
docker ps >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker is not running!
    echo Please start Docker Desktop first.
    echo.
    pause
    exit /b 1
)

echo Docker is running. Starting services...
echo.

echo Step 1: Building project...
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)
echo Build successful!
echo.

echo Step 2: Starting infrastructure services...
docker-compose up -d zookeeper kafka mysql kafka-ui
echo Waiting for infrastructure to be ready...
timeout /t 30 /nobreak >nul

echo Step 3: Starting microservices...
docker-compose up -d order-service payment-service notification-service
echo Waiting for services to start...
timeout /t 20 /nobreak >nul

echo.
echo ========================================
echo All services started!
echo ========================================
echo.
echo Service URLs:
echo - Order Service: http://localhost:8087
echo - Payment Service: http://localhost:8082
echo - Notification Service: http://localhost:8083
echo - Kafka UI: http://localhost:8081
echo.
echo To check service status: docker ps
echo To view logs: docker logs <service-name>
echo.
pause
