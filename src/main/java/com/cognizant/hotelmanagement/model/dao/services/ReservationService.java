package com.cognizant.hotelmanagement.model.dao.services;

import com.cognizant.hotelmanagement.model.pojo.Reservation;
import java.util.List;
import java.util.Optional;

public interface ReservationService {
    Reservation createReservation(Reservation reservation);
    Optional<Reservation> getReservationById(Long reservationId);
    List<Reservation> getReservationsByUserId(Long userId);
    List<Reservation> getReservationsByHotelId(Long hotelId);
    List<Reservation> getAllReservations();
    Reservation updateReservation(Long reservationId, Reservation reservation);
    void cancelReservation(Long reservationId);
    Double calculateTotalCost(Reservation reservation);
    Double calculateRefundAmount(Long reservationId);
}