package com.example.common.common_dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public class NotificationEvent {
    @NotBlank(message = "Notification ID is required")
    private String notificationId;
    
    @NotBlank(message = "Order ID is required")
    private String orderId;
    
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;
    
    @NotBlank(message = "Message is required")
    private String message;
    
    @NotBlank(message = "Status is required")
    private String status;

    // Default constructor
    public NotificationEvent() {
    }

    // All-args constructor
    public NotificationEvent(String notificationId, String orderId, String customerId, String customerEmail, String message, String status) {
        this.notificationId = notificationId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.message = message;
        this.status = status;
    }

    // Getters and Setters
    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}