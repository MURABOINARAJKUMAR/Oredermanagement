package com.example.paymentservice.service;

import com.example.common.common_dto.PaymentEvent;
import com.example.paymentservice.kafka.PaymentProducer;
import com.example.paymentservice.model.Payment;
import com.example.paymentservice.repository.PaymentRepository;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;

    public PaymentService(PaymentRepository paymentRepository, PaymentProducer paymentProducer) {
        this.paymentRepository = paymentRepository;
        this.paymentProducer = paymentProducer;
    }

    @Transactional
    public Payment processPayment(Payment payment) {
        Payment savedPayment = paymentRepository.save(payment);
        
        // Create and send payment event
        PaymentEvent event = new PaymentEvent();
        event.setPaymentId(savedPayment.getPaymentId());
        event.setOrderId(savedPayment.getOrderId());
        event.setCustomerId(savedPayment.getCustomerId());
        event.setAmount(savedPayment.getAmount());
        event.setStatus(savedPayment.getStatus());
        event.setPaymentMethod(savedPayment.getPaymentMethod());
        
        paymentProducer.sendMessage(event);
        
        return savedPayment;
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElseThrow(() -> 
                new RuntimeException("Payment not found with id: " + id));
    }

    public Payment getPaymentByPaymentId(String paymentId) {
        return paymentRepository.findByPaymentId(paymentId);
    }

    public List<Payment> getPaymentsByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
