package com.example.notificationservice.kafka;

import com.example.common.common_dto.OrderEvent;
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);
    private final NotificationService notificationService;

    public OrderConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "orders", groupId = "notification-group")
    public void consume(OrderEvent event) {
        try {
            LOGGER.info("Order event received in notification service => {}", event);
            
            // Create order confirmation notification
            Notification notification = new Notification();
            notification.setNotificationId(UUID.randomUUID().toString());
            notification.setOrderId(event.getOrderId());
            notification.setCustomerId(event.getCustomerId());
            notification.setCustomerEmail(event.getCustomerEmail());
            notification.setMessage("Your order has been created with ID: " + event.getOrderId());
            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
            
            notificationService.sendNotification(notification);
            LOGGER.info("Order notification sent successfully for order: {}", event.getOrderId());
        } catch (Exception e) {
            LOGGER.error("Error sending order notification for order: {}", event.getOrderId(), e);
            // In a real application, you might want to send to a dead letter queue
            throw e;
        }
    }
}
