package com.cognizant.paymentservice.service;

import com.cognizant.paymentservice.model.Payment;
import com.cognizant.paymentservice.model.PaymentStatus;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    
    Payment processPayment(Payment payment);
    
    Optional<Payment> getPaymentById(Long paymentId);
    
    List<Payment> getAllPayments();
    
    List<Payment> getPaymentsByUserId(Long userId);
    
    List<Payment> getPaymentsByReservationId(Long reservationId);
    
    Payment updatePayment(Long paymentId, Payment payment);
    
    void deletePayment(Long paymentId);
    
    List<Payment> getPaymentsByStatus(PaymentStatus status);
    
    Optional<Payment> getPaymentByTransactionId(String transactionId);
    
    Payment refundPayment(Long paymentId);
}
