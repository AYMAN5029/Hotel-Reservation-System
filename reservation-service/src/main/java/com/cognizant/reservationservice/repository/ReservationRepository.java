package com.cognizant.reservationservice.repository;

import com.cognizant.reservationservice.model.Reservation;
import com.cognizant.reservationservice.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    List<Reservation> findByUserId(Long userId);
    
    List<Reservation> findByHotelId(Long hotelId);
    
    List<Reservation> findByStatus(ReservationStatus status);
    
    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.hotelId = :hotelId AND r.status = :status AND " +
           "((r.checkInDate <= :checkOutDate AND r.checkOutDate >= :checkInDate))")
    List<Reservation> findConflictingReservations(@Param("hotelId") Long hotelId, 
                                                 @Param("checkInDate") LocalDate checkInDate,
                                                 @Param("checkOutDate") LocalDate checkOutDate,
                                                 @Param("status") ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.checkInDate >= :startDate AND r.checkInDate <= :endDate")
    List<Reservation> findReservationsByDateRange(@Param("startDate") LocalDate startDate, 
                                                  @Param("endDate") LocalDate endDate);
}
