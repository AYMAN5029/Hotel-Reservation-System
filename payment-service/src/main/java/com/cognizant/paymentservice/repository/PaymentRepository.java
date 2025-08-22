package com.cognizant.paymentservice.repository;

import com.cognizant.paymentservice.model.Payment;
import com.cognizant.paymentservice.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByUserId(Long userId);
    
    List<Payment> findByReservationId(Long reservationId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findByUserIdAndStatus(Long userId, PaymentStatus status);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByPaymentMethod(String paymentMethod);
}
