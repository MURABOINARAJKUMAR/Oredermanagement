# 🧪 Windows Testing Guide - Order Management System (Postman Focus)

This comprehensive guide provides Windows-specific testing instructions using **Postman** for the Order Management System:
- Order Service (8087)
- Payment Service (8082) 
- Notification Service (8083)
- Kafka, MySQL, and Docker

**Target Audience**: Windows users testing with Postman API client

## 📋 Table of Contents

1. Prerequisites & Setup
2. Postman Collection Setup
3. Service Health Verification
4. Authentication & User Management
5. Order Service API Testing
6. Payment Service API Testing
7. Notification Service API Testing
8. End-to-End Integration Testing
9. Database Verification (MySQL Workbench)
10. Performance Testing
11. Troubleshooting & Windows Tips

## 🚀 1) Prerequisites & Setup

### Required Software:
- **Docker Desktop** (Windows)
- **Postman** (Latest version)
- **MySQL Workbench** (Optional, for database inspection)
- **Java 21** (JAVA_HOME set)
- **Maven 3.9+**
- **8GB RAM** available

### Windows Verification Commands:
```powershell
# Docker
docker --version
docker ps

# Java
java --version

# Maven  
mvn --version

# Check available ports
netstat -an | findstr ":8087"
netstat -an | findstr ":8082" 
netstat -an | findstr ":8083"
```

## ▶️ 2) Start Services & Postman Collection Setup

### Start Services (Windows):
```powershell
# Navigate to project directory
cd C:\Users\Hp\OneDrive\Desktop\Project-hcl\Order_Management

# Start all services
docker-compose up -d --build

# Check container status
docker-compose ps
# Expected: All services running on ports 8087, 8082, 8083, 3308, 9092, 2181, 8081

# Wait for services to initialize
Start-Sleep -Seconds 30
```

### Postman Collection Setup:

1. **Create New Collection**: "Order Management System"
2. **Set Environment Variables**:
   - `base_url_order`: `http://localhost:8087`
   - `base_url_payment`: `http://localhost:8082`
   - `base_url_notification`: `http://localhost:8083`
   - `jwt_token`: (will be set after login)
   - `order_id`: (will be set after creating order)
   - `payment_id`: (will be set after payment)

3. **Collection Structure**:
   ```
   📁 Order Management System
   ├── 📁 1. Health Checks
   ├── 📁 2. Authentication
   ├── 📁 3. User Management
   ├── 📁 4. Order Service
   ├── 📁 5. Payment Service
   ├── 📁 6. Notification Service
   └── 📁 7. Integration Tests
   ```

## 🔍 3) Service Health Verification

### Postman Health Check Requests:

#### 1. Order Service Health Check
- **Method**: `GET`
- **URL**: `{{base_url_order}}/actuator/health`
- **Headers**: None required
- **Expected Response**: 
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "kafka": {"status": "UP"}
  }
}
```

#### 2. Payment Service Health Check
- **Method**: `GET`
- **URL**: `{{base_url_payment}}/actuator/health`
- **Headers**: None required
- **Expected Response**:
```json
{
      "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "kafka": {"status": "UP"}
  }
}
```

#### 3. Notification Service Health Check
- **Method**: `GET`
- **URL**: `{{base_url_notification}}/actuator/health`
- **Headers**: None required
- **Expected Response**:
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "kafka": {"status": "UP"}
  }
}
```

### Windows PowerShell Alternative:
```powershell
# Quick health check
curl.exe http://localhost:8087/actuator/health
curl.exe http://localhost:8082/actuator/health
curl.exe http://localhost:8083/actuator/health
```

**Analysis**: Health checks verify that all services are running and can connect to their dependencies (MySQL, Kafka). If any service shows "DOWN", check Docker logs.

## 🔐 4) Authentication & User Management

### Default Admin Credentials:
- **Username**: `admin`
- **Password**: `password`
- **Roles**: `ROLE_ADMIN`, `ROLE_USER`
- **Email**: `admin@example.com`

### Postman Authentication Requests:

#### 1. Admin Login
- **Method**: `POST`
- **URL**: `{{base_url_order}}/api/auth/login`
- **Headers**: 
  - `Content-Type: application/json`
- **Body** (raw JSON):
```json
{
    "username": "admin",
    "password": "password"
}
```
- **Expected Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "email": "admin@example.com",
  "roles": ["ROLE_ADMIN", "ROLE_USER"],
  "expiresIn": 86400
}
```

**Postman Test Script** (to auto-save token):
```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set("jwt_token", response.token);
    console.log("JWT Token saved:", response.token);
}
```

#### 2. Create New User (Admin Only)
- **Method**: `POST`
- **URL**: `{{base_url_order}}/api/users`
- **Headers**: 
  - `Content-Type: application/json`
  - `Authorization: Bearer {{jwt_token}}`
- **Body** (raw JSON):
```json
{
  "username": "testuser",
  "email": "testuser@example.com",
  "password": "StrongPass123",
  "roles": ["ROLE_USER"]
}
```
- **Expected Response**:
```json
{
  "id": 2,
  "username": "testuser",
  "email": "testuser@example.com",
  "roles": ["ROLE_USER"],
  "createdAt": "2025-01-08T10:30:00Z"
}
```

#### 3. Get Current User Profile
- **Method**: `GET`
- **URL**: `{{base_url_order}}/api/users/me`
- **Headers**: 
  - `Authorization: Bearer {{jwt_token}}`
- **Expected Response**:
```json
{
  "id": 1,
    "username": "admin",
  "email": "admin@example.com",
  "roles": ["ROLE_ADMIN", "ROLE_USER"],
  "createdAt": "2025-01-08T09:00:00Z"
}
```

#### 4. User Login (Test User)
- **Method**: `POST`
- **URL**: `{{base_url_order}}/api/auth/login`
- **Headers**: 
  - `Content-Type: application/json`
- **Body** (raw JSON):
```json
{
  "username": "testuser",
  "password": "StrongPass123"
}
```

**Analysis**: 
- JWT tokens expire in 24 hours (86400 seconds)
- Admin users can create other users
- Regular users can only access their own profile
- Password is hashed using BCrypt

## 📦 5) Order Service API Testing (Port 8087)

### Postman Order Service Requests:

#### 1. Create New Order
- **Method**: `POST`
- **URL**: `{{base_url_order}}/api/orders`
- **Headers**: 
  - `Content-Type: application/json`
- **Body** (raw JSON):
```json

```
- **Expected Response**:
```json
{
  "orderId": "ORD-20250108-001",
  "customerId": "CUST001",
  "customerName": "John Doe",
  "customerEmail": "john.doe@example.com",
  "status": "PENDING",
  "totalAmount": 1399.97,
  "items": [
    {
      "productId": "PROD001",
      "productName": "Gaming Laptop",
      "quantity": 1,
      "price": 1299.99,
      "subtotal": 1299.99
    },
    {
      "productId": "PROD002",
      "productName": "Wireless Mouse",
      "quantity": 2,
      "price": 49.99,
      "subtotal": 99.98
    }
  ],
  "createdAt": "2025-01-08T10:30:00Z",
  "updatedAt": "2025-01-08T10:30:00Z"
}
```

**Postman Test Script** (to save order ID):
```javascript
if (pm.response.code === 201) {
    const response = pm.response.json();
    pm.environment.set("order_id", response.orderId);
    console.log("Order ID saved:", response.orderId);
}
```

#### 2. Get All Orders
- **Method**: `GET`
- **URL**: `{{base_url_order}}/api/orders`
- **Headers**: None required
- **Expected Response**:
```json
[
  {
    "orderId": "ORD-20250108-001",
    "customerId": "CUST001",
    "customerName": "John Doe",
    "status": "PENDING",
    "totalAmount": 1399.97,
    "createdAt": "2025-01-08T10:30:00Z"
  }
]
```

#### 3. Get Order by ID
- **Method**: `GET`
- **URL**: `{{base_url_order}}/api/orders/{{order_id}}`
- **Headers**: None required
- **Expected Response**: Same as Create Order response

#### 4. Update Order Status
- **Method**: `PUT`
- **URL**: `{{base_url_order}}/api/orders/{{order_id}}/status`
- **Headers**: 
  - `Content-Type: application/json`
- **Body** (raw JSON):
```json
{
    "status": "PROCESSING"
}
```
- **Expected Response**:
```json
{
  "orderId": "ORD-20250108-001",
  "status": "PROCESSING",
  "updatedAt": "2025-01-08T10:35:00Z"
}
```

#### 5. Get Orders by Customer
- **Method**: `GET`
- **URL**: `{{base_url_order}}/api/orders/customer/CUST001`
- **Headers**: None required

**Analysis**:
- Orders are created with `PENDING` status by default
- Total amount is calculated automatically from items
- Order ID follows format: `ORD-YYYYMMDD-XXX`
- Kafka events are published for order creation and status updates

## 💳 6) Payment Service API Testing (Port 8082)

### Postman Payment Service Requests:

#### 1. Process Payment
- **Method**: `GET`
- **URL**: `{{base_url_payment}}/api/payments`
- **Headers**: 
  - `Content-Type: application/json`
- **Body** (raw JSON):
```json
{
  "orderId": "{{order_id}}",
  "amount": 1399.97,
    "paymentMethod": "CREDIT_CARD",
    "cardNumber": "1234-5678-9012-3456",
    "expiryDate": "12/25",
    "cvv": "123",
    "customerName": "John Doe"
}
```
- **Expected Response**:
```json
{
  "paymentId": "PAY-20250108-001",
  "orderId": "ORD-20250108-001",
  "amount": 1399.97,
  "paymentMethod": "CREDIT_CARD",
  "status": "COMPLETED",
  "transactionId": "TXN-123456789",
  "processedAt": "2025-01-08T10:35:00Z",
  "customerName": "John Doe"
}
```

**Postman Test Script** (to save payment ID):
```javascript
if (pm.response.code === 201) {
    const response = pm.response.json();
    pm.environment.set("payment_id", response.paymentId);
    console.log("Payment ID saved:", response.paymentId);
}
```

#### 2. Get Payment by ID
- **Method**: `GET`
- **URL**: `{{base_url_payment}}/api/payments/{{payment_id}}`
- **Headers**: None required
- **Expected Response**: Same as Process Payment response

#### 3. Get All Payments
- **Method**: `GET`
- **URL**: `{{base_url_payment}}/api/payments`
- **Headers**: None required
- **Expected Response**:
```json
[
  {
    "paymentId": "PAY-20250108-001",
    "orderId": "ORD-20250108-001",
    "amount": 1399.97,
    "paymentMethod": "CREDIT_CARD",
    "status": "COMPLETED",
    "processedAt": "2025-01-08T10:35:00Z"
  }
]
```

#### 4. Get Payments by Order ID
- **Method**: `GET`
- **URL**: `{{base_url_payment}}/api/payments/order/{{order_id}}`
- **Headers**: None required

#### 5. Process Failed Payment (Test Error Handling)
- **Method**: `POST`
- **URL**: `{{base_url_payment}}/api/payments`
- **Headers**: 
  - `Content-Type: application/json`
- **Body** (raw JSON):
```json
{
  "orderId": "{{order_id}}",
  "amount": 1399.97,
    "paymentMethod": "CREDIT_CARD",
    "cardNumber": "0000-0000-0000-0000",
    "expiryDate": "12/25",
  "cvv": "123",
  "customerName": "John Doe"
}
```
- **Expected Response** (Error):
```json
{
  "timestamp": "2025-01-08T10:35:00Z",
  "status": 400,
  "error": "Payment Failed",
  "message": "Invalid card number",
  "path": "/api/payments"
}
```

**Analysis**:
- Payment service validates card details
- Successful payments trigger Kafka events
- Payment status can be: `PENDING`, `COMPLETED`, `FAILED`
- Payment ID follows format: `PAY-YYYYMMDD-XXX`

## 📧 7) Notification Service API Testing (Port 8083)

### Postman Notification Service Requests:

#### 1. Get All Notifications
- **Method**: `GET`
- **URL**: `{{base_url_notification}}/api/notifications`
- **Headers**: None required
- **Expected Response**:
```json
[
  {
    "id": 1,
    "type": "ORDER_CREATED",
    "recipient": "john.doe@example.com",
    "subject": "Order Confirmation",
    "message": "Your order ORD-20250108-001 has been created successfully.",
    "orderId": "ORD-20250108-001",
    "sentAt": "2025-01-08T10:30:00Z",
    "status": "SENT"
  },
  {
    "id": 2,
    "type": "PAYMENT_COMPLETED",
    "recipient": "john.doe@example.com",
    "subject": "Payment Confirmation",
    "message": "Payment for order ORD-20250108-001 has been processed successfully.",
    "orderId": "ORD-20250108-001",
    "sentAt": "2025-01-08T10:35:00Z",
    "status": "SENT"
  }
]
```

#### 2. Get Notifications by Type
- **Method**: `GET`
- **URL**: `{{base_url_notification}}/api/notifications?type=ORDER_CREATED`
- **Headers**: None required
- **Expected Response**: Array of notifications with type "ORDER_CREATED"

#### 3. Get Notifications by Recipient
- **Method**: `GET`
- **URL**: `{{base_url_notification}}/api/notifications?recipient=john.doe@example.com`
- **Headers**: None required
- **Expected Response**: Array of notifications for the specified email

#### 4. Get Notifications by Order ID
- **Method**: `GET`
- **URL**: `{{base_url_notification}}/api/notifications?orderId={{order_id}}`
- **Headers**: None required
- **Expected Response**: Array of notifications related to the specific order

#### 5. Get Notification by ID
- **Method**: `GET`
- **URL**: `{{base_url_notification}}/api/notifications/1`
- **Headers**: None required
- **Expected Response**: Single notification object

#### 6. Get Notification Statistics
- **Method**: `GET`
- **URL**: `{{base_url_notification}}/api/notifications/stats`
- **Headers**: None required
- **Expected Response**:
```json
{
  "totalNotifications": 2,
  "sentNotifications": 2,
  "failedNotifications": 0,
  "notificationsByType": {
    "ORDER_CREATED": 1,
    "PAYMENT_COMPLETED": 1
  }
}
```

**Analysis**:
- Notifications are automatically created when orders and payments are processed
- Notification types: `ORDER_CREATED`, `PAYMENT_COMPLETED`, `ORDER_UPDATED`
- Notifications are sent via Kafka events from other services
- Status can be: `PENDING`, `SENT`, `FAILED`

## 🔄 8) End-to-End Integration Testing

### Complete Workflow Testing in Postman:

#### **Step 1: Create Order**
1. **Request**: Create New Order (from Section 5)
2. **Verify**: Order created with `PENDING` status
3. **Check**: Notification service receives `ORDER_CREATED` event

#### **Step 2: Process Payment**
1. **Request**: Process Payment (from Section 6)
2. **Verify**: Payment completed successfully
3. **Check**: Order status updated to `PROCESSING`

#### **Step 3: Verify Notifications**
1. **Request**: Get All Notifications (from Section 7)
2. **Verify**: Both `ORDER_CREATED` and `PAYMENT_COMPLETED` notifications exist
3. **Check**: Notifications sent to correct email address

#### **Step 4: Update Order Status**
1. **Request**: Update Order Status to `COMPLETED`
2. **Verify**: Order status changed
3. **Check**: New notification created for status update

### **Postman Collection Runner Setup:**

1. **Create Test Collection** with this sequence:
   ```
   📁 Integration Test Flow
   ├── 1. Health Check (Order Service)
   ├── 2. Health Check (Payment Service)
   ├── 3. Health Check (Notification Service)
   ├── 4. Admin Login
   ├── 5. Create Order
   ├── 6. Process Payment
   ├── 7. Get Notifications
   ├── 8. Update Order Status
   └── 9. Verify Final State
   ```

2. **Collection Runner Configuration**:
   - **Iterations**: 1
   - **Delay**: 2000ms between requests
   - **Environment**: Use your created environment

3. **Test Scripts** (add to each request):
```javascript
// Global test script for all requests
pm.test("Response time is less than 5000ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(5000);
});

pm.test("Response has valid JSON", function () {
    pm.response.to.be.json;
});
```

### **Expected Integration Flow Results:**

1. **Order Creation** → Kafka Event → Notification Created
2. **Payment Processing** → Kafka Event → Order Status Updated → Notification Created
3. **Order Status Update** → Kafka Event → Notification Created

### **Windows PowerShell Integration Test:**
```powershell
# Quick integration test
$orderBody = @{
    customerId = "CUST001"
    customerName = "Integration Test User"
    customerEmail = "integration@example.com"
    items = @(
        @{
            productId = "PROD001"
            productName = "Test Product"
            quantity = 1
            price = 100.00
        }
    )
} | ConvertTo-Json

# Create order
$orderResponse = irm -Method Post -Uri 'http://localhost:8087/api/orders' -ContentType 'application/json' -Body $orderBody
$orderId = ($orderResponse | ConvertFrom-Json).orderId

# Process payment
$paymentBody = @{
    orderId = $orderId
    amount = 100.00
    paymentMethod = "CREDIT_CARD"
    cardNumber = "1234-5678-9012-3456"
    expiryDate = "12/25"
    cvv = "123"
    customerName = "Integration Test User"
} | ConvertTo-Json

$paymentResponse = irm -Method Post -Uri 'http://localhost:8082/api/payments' -ContentType 'application/json' -Body $paymentBody

# Wait and check notifications
Start-Sleep -Seconds 5
$notifications = irm -Uri 'http://localhost:8083/api/notifications'
Write-Host "Integration test completed. Notifications: $($notifications.Count)"
```

## 🗄️ 9) Database Verification (MySQL Workbench)

### **MySQL Workbench Connection:**
- **Hostname**: `localhost`
- **Port**: `3308`
- **Username**: `root`
- **Password**: `4186`

### **Database Structure Verification:**

#### **Order Database (order_db):**
```sql
USE order_db;
SHOW TABLES;
-- Expected: users, user_roles, orders, order_items

-- Check users
SELECT u.id, u.username, u.email, GROUP_CONCAT(ur.role) as roles
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
GROUP BY u.id, u.username, u.email;

-- Check orders
SELECT orderId, customerId, customerName, status, totalAmount, createdAt
FROM orders
ORDER BY createdAt DESC;

-- Check order items
SELECT oi.id, oi.orderId, oi.productId, oi.productName, oi.quantity, oi.price
FROM order_items oi
JOIN orders o ON oi.orderId = o.orderId
ORDER BY oi.id DESC;
```

#### **Payment Database (payment_db):**
```sql
USE payment_db;
SHOW TABLES;
-- Expected: payments

-- Check payments
SELECT paymentId, orderId, amount, paymentMethod, status, processedAt
FROM payments
ORDER BY processedAt DESC;
```

#### **Notification Database (notification_db):**
```sql
USE notification_db;
SHOW TABLES;
-- Expected: notifications

-- Check notifications
SELECT id, type, recipient, subject, orderId, sentAt, status
FROM notifications
ORDER BY sentAt DESC;
```

### **Windows PowerShell Database Checks:**
```powershell
# Quick database verification
docker exec -it mysql mysql -u root -p4186 -e "USE order_db; SELECT COUNT(*) as user_count FROM users;"
docker exec -it mysql mysql -u root -p4186 -e "USE order_db; SELECT COUNT(*) as order_count FROM orders;"
docker exec -it mysql mysql -u root -p4186 -e "USE payment_db; SELECT COUNT(*) as payment_count FROM payments;"
docker exec -it mysql mysql -u root -p4186 -e "USE notification_db; SELECT COUNT(*) as notification_count FROM notifications;"
```

## ⚡ 10) Performance Testing

### **Postman Performance Tests:**

#### **1. Response Time Testing:**
- Use Postman's **Collection Runner** with multiple iterations
- Set **Delay**: 100ms between requests
- Monitor **Response Time** in test results

#### **2. Load Testing Setup:**
1. **Create Performance Collection** with:
   - Create Order (10 iterations)
   - Process Payment (10 iterations)
   - Get Notifications (10 iterations)

2. **Collection Runner Configuration**:
   - **Iterations**: 10
   - **Delay**: 100ms
   - **Data File**: CSV with test data

#### **3. Performance Test Scripts:**
```javascript
// Response time test
pm.test("Response time is acceptable", function () {
    pm.expect(pm.response.responseTime).to.be.below(2000);
});

// Memory usage test
pm.test("Response size is reasonable", function () {
    pm.expect(pm.response.responseSize).to.be.below(10000);
});
```

### **Windows PowerShell Load Test:**
```powershell
# Create 10 orders for load testing
for ($i = 1; $i -le 10; $i++) {
    $orderBody = @{
        customerId = "CUST$i"
        customerName = "Load Test Customer $i"
        customerEmail = "loadtest$i@example.com"
        items = @(
            @{
                productId = "PROD$i"
                productName = "Load Test Product $i"
                quantity = 1
                price = (Get-Random -Minimum 50 -Maximum 500)
            }
        )
    } | ConvertTo-Json
    
    $startTime = Get-Date
    $response = irm -Method Post -Uri 'http://localhost:8087/api/orders' -ContentType 'application/json' -Body $orderBody
    $endTime = Get-Date
    $duration = ($endTime - $startTime).TotalMilliseconds
    
    Write-Host "Order $i created in $duration ms"
    Start-Sleep -Milliseconds 100
}
```

### **Performance Benchmarks:**
- **Order Creation**: < 500ms
- **Payment Processing**: < 1000ms
- **Notification Retrieval**: < 200ms
- **Database Queries**: < 100ms

## 🧰 11) Troubleshooting & Windows Tips

### **Common Issues & Solutions:**

#### **1. Service Connection Issues:**
- **Problem**: 400 Bad Request on health checks
- **Solution**: Use `curl.exe` instead of `curl` in PowerShell
- **Command**: `curl.exe http://localhost:8087/actuator/health`

#### **2. Port Conflicts:**
- **Problem**: Port already in use
- **Solution**: Find and kill processes
```powershell
# Find process using port
netstat -ano | findstr ":8087"
# Kill process (replace PID with actual process ID)
taskkill /PID <PID> /F
```

#### **3. Docker Container Issues:**
- **Problem**: Containers not starting
- **Solution**: Check logs and restart
```powershell
# Check container status
docker-compose ps

# View logs
docker-compose logs order-service
docker-compose logs payment-service
docker-compose logs notification-service

# Restart specific service
docker-compose restart order-service
```

#### **4. Database Connection Issues:**
- **Problem**: MySQL connection failed
- **Solution**: Verify container and credentials
```powershell
# Check MySQL container
docker ps | findstr mysql

# Test connection
docker exec -it mysql mysql -u root -p4186 -e "SELECT 1;"
```

#### **5. JWT Token Issues:**
- **Problem**: 401 Unauthorized
- **Solution**: Re-login and update token
- **Steps**: 
  1. Login again in Postman
  2. Copy new token
  3. Update environment variable

### **Windows-Specific Tips:**

#### **PowerShell Commands:**
```powershell
# Use curl.exe for API calls
curl.exe -X POST http://localhost:8087/api/auth/login -H "Content-Type: application/json" -d '{"username":"admin","password":"password"}'

# Use irm for PowerShell-native requests
irm -Method Post -Uri 'http://localhost:8087/api/auth/login' -ContentType 'application/json' -Body $body

# Check port availability
netstat -an | findstr ":8087"
```

#### **Postman Environment Setup:**
1. **Create Environment**: "Order Management Local"
2. **Set Variables**:
   - `base_url_order`: `http://localhost:8087`
   - `base_url_payment`: `http://localhost:8082`
   - `base_url_notification`: `http://localhost:8083`
   - `jwt_token`: (auto-populated after login)

#### **Quick Health Check Script:**
```powershell
# Save as health-check.ps1
Write-Host "Checking Order Management System Health..." -ForegroundColor Green

$services = @(
    @{Name="Order Service"; URL="http://localhost:8087/actuator/health"},
    @{Name="Payment Service"; URL="http://localhost:8082/actuator/health"},
    @{Name="Notification Service"; URL="http://localhost:8083/actuator/health"}
)

foreach ($service in $services) {
    try {
        $response = irm -Uri $service.URL -UseBasicParsing
        if ($response.status -eq "UP") {
            Write-Host "✅ $($service.Name): UP" -ForegroundColor Green
        } else {
            Write-Host "❌ $($service.Name): DOWN" -ForegroundColor Red
        }
    } catch {
        Write-Host "❌ $($service.Name): ERROR - $($_.Exception.Message)" -ForegroundColor Red
    }
}
```

### **Performance Monitoring:**
```powershell
# Monitor Docker resource usage
docker stats

# Check container logs in real-time
docker-compose logs -f order-service
```

### **Database Backup (Optional):**
```powershell
# Backup databases
docker exec mysql mysqldump -u root -p4186 order_db > order_db_backup.sql
docker exec mysql mysqldump -u root -p4186 payment_db > payment_db_backup.sql
docker exec mysql mysqldump -u root -p4186 notification_db > notification_db_backup.sql
```

---

## 🎯 **Quick Start Checklist:**

1. ✅ **Start Services**: `docker-compose up -d --build`
2. ✅ **Health Check**: Run health-check.ps1 or use Postman
3. ✅ **Login**: Admin login in Postman
4. ✅ **Create Order**: Test order creation
5. ✅ **Process Payment**: Test payment processing
6. ✅ **Check Notifications**: Verify notifications created
7. ✅ **Database Check**: Verify data in MySQL Workbench

**Happy Testing! 🎉**

*This guide provides comprehensive Windows-focused testing using Postman with detailed examples, troubleshooting tips, and performance benchmarks.*

