package com.cognizant.hotelmanagement.model.dao.services;

import com.cognizant.hotelmanagement.model.pojo.Payment;
import java.util.Optional;

public interface PaymentService {
    Payment processPayment(Payment payment);
    Optional<Payment> getPaymentById(Long paymentId);
    Optional<Payment> getPaymentByReservationId(Long reservationId);
    Payment updatePaymentStatus(Long paymentId, Payment.PaymentStatus status);
    Payment processRefund(Long paymentId, Double refundAmount);
}