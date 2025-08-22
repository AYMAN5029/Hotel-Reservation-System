package com.cognizant.reservationservice.service;

import com.cognizant.reservationservice.model.Reservation;
import com.cognizant.reservationservice.model.ReservationStatus;

import java.util.List;
import java.util.Optional;

public interface ReservationService {
    
    Reservation createReservation(Reservation reservation);
    
    Optional<Reservation> getReservationById(Long reservationId);
    
    List<Reservation> getAllReservations();
    
    List<Reservation> getReservationsByUserId(Long userId);
    
    List<Reservation> getReservationsByHotelId(Long hotelId);
    
    Reservation updateReservation(Long reservationId, Reservation reservation);
    
    Reservation cancelReservation(Long reservationId);
    
    void deleteReservation(Long reservationId);
    
    List<Reservation> getReservationsByStatus(ReservationStatus status);
    
    Reservation confirmReservation(Long reservationId);
    
    void deleteReservationsByHotelId(Long hotelId);
}
