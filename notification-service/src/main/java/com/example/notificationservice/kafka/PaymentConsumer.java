package com.example.notificationservice.kafka;

import com.example.common.common_dto.PaymentEvent;
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentConsumer.class);
    private final NotificationService notificationService;

    public PaymentConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "payments", groupId = "notification-group")
    public void consume(PaymentEvent event) {
        try {
            LOGGER.info("Payment event received in notification service => {}", event);
            
            // Create payment confirmation notification
            Notification notification = new Notification();
            notification.setNotificationId(UUID.randomUUID().toString());
            notification.setOrderId(event.getOrderId());
            notification.setCustomerId(event.getCustomerId());
            notification.setCustomerEmail("customer@example.com"); // In real app, fetch from DB
            notification.setMessage("Payment for order " + event.getOrderId() + " is " + event.getStatus());
            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
            
            notificationService.sendNotification(notification);
            LOGGER.info("Payment notification sent successfully for payment: {}", event.getPaymentId());
        } catch (Exception e) {
            LOGGER.error("Error sending payment notification for payment: {}", event.getPaymentId(), e);
            // In a real application, you might want to send to a dead letter queue
            throw e;
        }
    }
}