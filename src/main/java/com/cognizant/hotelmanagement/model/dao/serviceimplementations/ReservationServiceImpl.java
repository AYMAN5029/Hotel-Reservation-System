package com.cognizant.hotelmanagement.model.dao.serviceimplementations;

import com.cognizant.hotelmanagement.model.dao.repositories.ReservationRepository;
import com.cognizant.hotelmanagement.model.dao.services.ReservationService;
import com.cognizant.hotelmanagement.model.exceptions.ReservationNotFoundException;
import com.cognizant.hotelmanagement.model.pojo.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Override
    public Reservation createReservation(Reservation reservation) {
        reservation.setTotalCost(calculateTotalCost(reservation));
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
        return reservationRepository.save(reservation);
    }
    
    @Override
    public Optional<Reservation> getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }
    
    @Override
    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserUserId(userId);
    }
    
    @Override
    public List<Reservation> getReservationsByHotelId(Long hotelId) {
        return reservationRepository.findByHotelHotelId(hotelId);
    }
    
    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    
    @Override
    public Reservation updateReservation(Long reservationId, Reservation updatedReservation) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with id: " + reservationId));
        
        reservation.setCheckInDate(updatedReservation.getCheckInDate());
        reservation.setCheckOutDate(updatedReservation.getCheckOutDate());
        reservation.setRoomType(updatedReservation.getRoomType());
        reservation.setNumberOfRooms(updatedReservation.getNumberOfRooms());
        reservation.setNumberOfGuests(updatedReservation.getNumberOfGuests());
        reservation.setTotalCost(calculateTotalCost(reservation));
        reservation.setUpdatedAt(LocalDateTime.now());
        
        return reservationRepository.save(reservation);
    }
    
    @Override
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with id: " + reservationId));
        
        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservation.setUpdatedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
    }
    
    @Override
    public Double calculateTotalCost(Reservation reservation) {
        long numberOfDays = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
        double costPerRoom = reservation.getRoomType() == Reservation.RoomType.AC 
                ? reservation.getHotel().getAcRoomCost() 
                : reservation.getHotel().getNonAcRoomCost();
        
        return numberOfDays * reservation.getNumberOfRooms() * costPerRoom;
    }
    
    @Override
    public Double calculateRefundAmount(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with id: " + reservationId));
        
        long daysBeforeCheckIn = ChronoUnit.DAYS.between(LocalDateTime.now().toLocalDate(), reservation.getCheckInDate());
        
        if (daysBeforeCheckIn >= 7) {
            return reservation.getTotalCost(); // Full refund
        } else if (daysBeforeCheckIn >= 3) {
            return reservation.getTotalCost() * 0.75; // 75% refund
        } else if (daysBeforeCheckIn >= 1) {
            return reservation.getTotalCost() * 0.50; // 50% refund
        } else {
            return 0.0; // No refund
        }
    }
}