package com.cognizant.hotelmanagement.model.dao.serviceimplementations;

import com.cognizant.hotelmanagement.model.dao.repositories.PaymentRepository;
import com.cognizant.hotelmanagement.model.dao.repositories.ReservationRepository;
import com.cognizant.hotelmanagement.model.dao.services.PaymentService;
import com.cognizant.hotelmanagement.model.exceptions.PaymentNotFoundException;
import com.cognizant.hotelmanagement.model.pojo.Payment;
import com.cognizant.hotelmanagement.model.pojo.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Override
    public Payment processPayment(Payment payment) {
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }
    
    @Override
    public Optional<Payment> getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId);
    }
    
    @Override
    public Optional<Payment> getPaymentByReservationId(Long reservationId) {
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);
        if (reservation.isPresent()) {
            return paymentRepository.findByReservation(reservation.get());
        }
        return Optional.empty();
    }
    
    @Override
    public Payment updatePaymentStatus(Long paymentId, Payment.PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));
        
        payment.setStatus(status);
        payment.setUpdatedAt(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }
    
    @Override
    public Payment processRefund(Long paymentId, Double refundAmount) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));
        
        payment.setRefundAmount(refundAmount);
        payment.setRefundDate(LocalDateTime.now());
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        payment.setUpdatedAt(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }
}