@echo off
echo Checking Order Management System Services...
echo.

echo Checking Docker containers...
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo.

echo Checking service endpoints...
echo.

echo Checking Order Service (Port 8087)...
curl -s -o nul -w "Order Service: %%{http_code}\n" http://localhost:8087/actuator/health 2>nul || echo "Order Service: Not accessible"

echo Checking Payment Service (Port 8082)...
curl -s -o nul -w "Payment Service: %%{http_code}\n" http://localhost:8082/actuator/health 2>nul || echo "Payment Service: Not accessible"

echo Checking Notification Service (Port 8083)...
curl -s -o nul -w "Notification Service: %%{http_code}\n" http://localhost:8083/actuator/health 2>nul || echo "Notification Service: Not accessible"

echo Checking Kafka UI (Port 8081)...
curl -s -o nul -w "Kafka UI: %%{http_code}\n" http://localhost:8081 2>nul || echo "Kafka UI: Not accessible"

echo.
echo Health check completed!
pause
