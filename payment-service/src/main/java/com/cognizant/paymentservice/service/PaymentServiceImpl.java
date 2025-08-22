package com.cognizant.paymentservice.service;

import com.cognizant.paymentservice.client.ReservationClient;
import com.cognizant.paymentservice.model.Payment;
import com.cognizant.paymentservice.model.PaymentStatus;
import com.cognizant.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private ReservationClient reservationClient;
    
    @Override
    public Payment processPayment(Payment payment) {
        try {
            // Mock payment processing - accepts any valid input format
            // Generate a mock transaction ID
            payment.setTransactionId("TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            
            // Mock validation based on payment method
            boolean isValidPayment = validatePaymentDetails(payment);
            
            if (isValidPayment) {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setDescription("Payment processed successfully");
                
                // Save payment first
                Payment savedPayment = paymentRepository.save(payment);
                
                // Then confirm the reservation
                try {
                    boolean reservationConfirmed = reservationClient.confirmReservation(payment.getReservationId());
                    if (!reservationConfirmed) {
                        System.err.println("Warning: Payment successful but failed to confirm reservation " + payment.getReservationId());
                        // You might want to implement compensation logic here
                    } else {
                        System.out.println("Successfully confirmed reservation " + payment.getReservationId());
                    }
                } catch (Exception e) {
                    System.err.println("Error confirming reservation " + payment.getReservationId() + ": " + e.getMessage());
                    e.printStackTrace();
                    // Payment was successful, but reservation confirmation failed
                    // In a production system, you might want to implement retry logic or compensation
                }
                
                return savedPayment;
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setDescription("Invalid payment details");
            }
            
            payment.setCreatedAt(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());
            
            return paymentRepository.save(payment);
            
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setDescription("Payment processing failed: " + e.getMessage());
            return paymentRepository.save(payment);
        }
    }
    
    private boolean validatePaymentDetails(Payment payment) {
        // Mock validation - accepts any valid input format as requested
        String paymentMethod = payment.getPaymentMethod().toUpperCase();
        
        switch (paymentMethod) {
            case "CREDIT_CARD":
            case "DEBIT_CARD":
                // Accept any card number format (minimum 10 digits)
                return payment.getCardNumber() != null && 
                       payment.getCardNumber().replaceAll("\\D", "").length() >= 10 &&
                       payment.getCardHolderName() != null && !payment.getCardHolderName().trim().isEmpty() &&
                       payment.getExpiryMonth() != null && !payment.getExpiryMonth().trim().isEmpty() &&
                       payment.getExpiryYear() != null && !payment.getExpiryYear().trim().isEmpty() &&
                       payment.getCvv() != null && payment.getCvv().length() >= 3;
                       
            case "UPI":
                // Accept any UPI ID format (contains @ symbol)
                return payment.getUpiId() != null && payment.getUpiId().contains("@");
                
            case "NET_BANKING":
                // Accept any bank name
                return payment.getBankName() != null && !payment.getBankName().trim().isEmpty();
                
            default:
                // Accept any other payment method as long as basic details are provided
                return payment.getAmount() != null && payment.getAmount() > 0;
        }
    }
    
    @Override
    public Optional<Payment> getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId);
    }
    
    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    @Override
    public List<Payment> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId);
    }
    
    @Override
    public List<Payment> getPaymentsByReservationId(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId);
    }
    
    @Override
    public Payment updatePayment(Long paymentId, Payment payment) {
        Optional<Payment> existingPayment = paymentRepository.findById(paymentId);
        if (existingPayment.isPresent()) {
            Payment updatedPayment = existingPayment.get();
            updatedPayment.setStatus(payment.getStatus());
            updatedPayment.setDescription(payment.getDescription());
            updatedPayment.setUpdatedAt(LocalDateTime.now());
            return paymentRepository.save(updatedPayment);
        }
        throw new RuntimeException("Payment not found with id: " + paymentId);
    }
    
    @Override
    public void deletePayment(Long paymentId) {
        paymentRepository.deleteById(paymentId);
    }
    
    @Override
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }
    
    @Override
    public Optional<Payment> getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }
    
    @Override
    public Payment refundPayment(Long paymentId) {
        Optional<Payment> existingPayment = paymentRepository.findById(paymentId);
        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();
            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                payment.setStatus(PaymentStatus.REFUNDED);
                payment.setDescription("Payment refunded successfully");
                payment.setUpdatedAt(LocalDateTime.now());
                return paymentRepository.save(payment);
            } else {
                throw new RuntimeException("Cannot refund payment with status: " + payment.getStatus());
            }
        }
        throw new RuntimeException("Payment not found with id: " + paymentId);
    }
}
