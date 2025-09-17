package com.example.paymentservice.kafka;

import com.example.common.common_dto.OrderEvent;
//import com.example.common.common_dto.PaymentEvent;
import com.example.paymentservice.model.Payment;
//import com.example.paymentservice.repository.PaymentRepository;
import com.example.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);
    private final PaymentService paymentService;

    public OrderConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "orders", groupId = "payment-group")
    public void consume(OrderEvent event) {
        try {
            LOGGER.info("Order event received in payment service => {}", event);
            
            // Process payment
            Payment payment = new Payment();
            payment.setPaymentId(UUID.randomUUID().toString());
            payment.setOrderId(event.getOrderId());
            payment.setCustomerId(event.getCustomerId());
            payment.setAmount(event.getTotalAmount());
            payment.setPaymentMethod("CREDIT_CARD");
            payment.setPaymentDate(LocalDateTime.now());
            
            // Simulate payment processing
            if (event.getTotalAmount() > 0) {
                payment.setStatus("COMPLETED");
            } else {
                payment.setStatus("FAILED");
            }
            
            paymentService.processPayment(payment);
            LOGGER.info("Payment processed successfully for order: {}", event.getOrderId());
        } catch (Exception e) {
            LOGGER.error("Error processing payment for order: {}", event.getOrderId(), e);
            // In a real application, you might want to send to a dead letter queue
            throw e;
        }
    }
}