package com.example.notificationservice.service;

import java.util.List; // Add this at the top
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Notification sendNotification(Notification notification) {
        // In a real application, you would integrate with email/SMS service here
        System.out.println("Sending notification to " + notification.getCustomerEmail() + 
                ": " + notification.getMessage());
        
        return notificationRepository.save(notification);
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id).orElseThrow(() -> 
                new RuntimeException("Notification not found with id: " + id));
    }

    public Notification getNotificationByNotificationId(String notificationId) {
        return notificationRepository.findByNotificationId(notificationId);
    }

    public List<Notification> getNotificationsByOrderId(String orderId) {
        return notificationRepository.findByOrderId(orderId);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
}