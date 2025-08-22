package com.cognizant.hotelmanagement.controllers;

import com.cognizant.hotelmanagement.model.dao.services.PaymentService;
import com.cognizant.hotelmanagement.model.dao.services.ReservationService;
import com.cognizant.hotelmanagement.model.pojo.Payment;
import com.cognizant.hotelmanagement.model.pojo.Reservation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private ReservationService reservationService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<?> processPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        try {
            Optional<Reservation> reservation = reservationService.getReservationById(paymentRequest.getReservationId());
            if (!reservation.isPresent()) {
                return new ResponseEntity<>("Reservation not found", HttpStatus.NOT_FOUND);
            }
            
            Payment payment = new Payment(
                reservation.get(),
                paymentRequest.getAmount(),
                paymentRequest.getPaymentMethod()
            );
            
            Payment processedPayment = paymentService.processPayment(payment);
            return new ResponseEntity<>(processedPayment, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<?> getPaymentById(@PathVariable Long paymentId) {
        Optional<Payment> payment = paymentService.getPaymentById(paymentId);
        if (payment.isPresent()) {
            return new ResponseEntity<>(payment.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Payment not found", HttpStatus.NOT_FOUND);
    }
    
    @GetMapping("/reservation/{reservationId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<?> getPaymentByReservationId(@PathVariable Long reservationId) {
        Optional<Payment> payment = paymentService.getPaymentByReservationId(reservationId);
        if (payment.isPresent()) {
            return new ResponseEntity<>(payment.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Payment not found for this reservation", HttpStatus.NOT_FOUND);
    }
    
    @PutMapping("/{paymentId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable Long paymentId, 
                                                @RequestBody PaymentStatusRequest statusRequest) {
        try {
            Payment updatedPayment = paymentService.updatePaymentStatus(paymentId, statusRequest.getStatus());
            return new ResponseEntity<>(updatedPayment, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("/{paymentId}/refund")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<?> processRefund(@PathVariable Long paymentId, 
                                         @RequestBody RefundRequest refundRequest) {
        try {
            Payment refundedPayment = paymentService.processRefund(paymentId, refundRequest.getRefundAmount());
            return new ResponseEntity<>(refundedPayment, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    // Inner classes for request DTOs
    public static class PaymentRequest {
        private Long reservationId;
        private Double amount;
        private Payment.PaymentMethod paymentMethod;
        
        // Getters and Setters
        public Long getReservationId() { return reservationId; }
        public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
        
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        
        public Payment.PaymentMethod getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(Payment.PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    }
    
    public static class PaymentStatusRequest {
        private Payment.PaymentStatus status;
        
        // Getters and Setters
        public Payment.PaymentStatus getStatus() { return status; }
        public void setStatus(Payment.PaymentStatus status) { this.status = status; }
    }
    
    public static class RefundRequest {
        private Double refundAmount;
        
        // Getters and Setters
        public Double getRefundAmount() { return refundAmount; }
        public void setRefundAmount(Double refundAmount) { this.refundAmount = refundAmount; }
    }
}