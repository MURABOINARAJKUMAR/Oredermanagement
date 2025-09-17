# Order Management System Startup Script (PowerShell)
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Order Management System Startup Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check Docker status
Write-Host "Checking Docker status..." -ForegroundColor Yellow
try {
    docker ps | Out-Null
    Write-Host "Docker is running." -ForegroundColor Green
} catch {
    Write-Host "ERROR: Docker is not running!" -ForegroundColor Red
    Write-Host "Please start Docker Desktop first." -ForegroundColor Red
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Docker is running. Starting services..." -ForegroundColor Green
Write-Host ""

# Step 1: Build project
Write-Host "Step 1: Building project..." -ForegroundColor Yellow
try {
    mvn clean compile -q
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Build successful!" -ForegroundColor Green
    } else {
        Write-Host "ERROR: Build failed!" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
} catch {
    Write-Host "ERROR: Build failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host ""

# Step 2: Start infrastructure services
Write-Host "Step 2: Starting infrastructure services..." -ForegroundColor Yellow
docker-compose up -d zookeeper kafka mysql kafka-ui
Write-Host "Waiting for infrastructure to be ready..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Step 3: Start microservices
Write-Host "Step 3: Starting microservices..." -ForegroundColor Yellow
docker-compose up -d order-service payment-service notification-service
Write-Host "Waiting for services to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 20

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "All services started!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Service URLs:" -ForegroundColor White
Write-Host "- Order Service: http://localhost:8087" -ForegroundColor Green
Write-Host "- Payment Service: http://localhost:8082" -ForegroundColor Green
Write-Host "- Notification Service: http://localhost:8083" -ForegroundColor Green
Write-Host "- Kafka UI: http://localhost:8081" -ForegroundColor Green
Write-Host ""

Write-Host "To check service status: docker ps" -ForegroundColor White
Write-Host "To view logs: docker logs <service-name>" -ForegroundColor White
Write-Host ""

Read-Host "Press Enter to exit"
