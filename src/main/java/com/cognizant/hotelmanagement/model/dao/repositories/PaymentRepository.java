package com.cognizant.hotelmanagement.model.dao.repositories;

import com.cognizant.hotelmanagement.model.pojo.Payment;
import com.cognizant.hotelmanagement.model.pojo.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservation(Reservation reservation);
    Optional<Payment> findByTransactionId(String transactionId);
}