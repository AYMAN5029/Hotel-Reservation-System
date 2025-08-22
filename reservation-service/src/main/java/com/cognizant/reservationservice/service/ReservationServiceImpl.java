package com.cognizant.reservationservice.service;

import com.cognizant.reservationservice.client.HotelServiceClient;
import com.cognizant.reservationservice.model.Reservation;
import com.cognizant.reservationservice.model.ReservationStatus;
import com.cognizant.reservationservice.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private HotelServiceClient hotelServiceClient;
    
    @Override
    public Reservation createReservation(Reservation reservation) {
        // Check room availability before creating reservation
        try {
            boolean isAvailable = hotelServiceClient.checkRoomAvailability(
                reservation.getHotelId(), 
                reservation.getRoomType(), 
                reservation.getNumberOfRooms()
            );
            
            if (!isAvailable) {
                throw new RuntimeException("Insufficient rooms available for the requested reservation");
            }
            
            // Update room availability (decrease available rooms)
            boolean updateSuccess = hotelServiceClient.updateRoomAvailability(
                reservation.getHotelId(),
                reservation.getRoomType(),
                reservation.getNumberOfRooms(),
                true // isReservation = true (making a reservation)
            );
            
            if (!updateSuccess) {
                throw new RuntimeException("Failed to update room availability");
            }
            
        } catch (Exception e) {
            System.err.println("Error checking/updating room availability: " + e.getMessage());
            throw new RuntimeException("Room availability check failed: " + e.getMessage());
        }
        
        // Set default values
        if (reservation.getStatus() == null) {
            reservation.setStatus(ReservationStatus.PENDING); // Changed to PENDING initially
        }
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
        
        return reservationRepository.save(reservation);
    }
    
    @Override
    public Optional<Reservation> getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }
    
    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    
    @Override
    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }
    
    @Override
    public List<Reservation> getReservationsByHotelId(Long hotelId) {
        return reservationRepository.findByHotelId(hotelId);
    }
    
    @Override
    public Reservation updateReservation(Long reservationId, Reservation reservation) {
        Optional<Reservation> existingReservation = reservationRepository.findById(reservationId);
        if (existingReservation.isPresent()) {
            Reservation reservationToUpdate = existingReservation.get();
            reservationToUpdate.setUserId(reservation.getUserId());
            reservationToUpdate.setHotelId(reservation.getHotelId());
            reservationToUpdate.setRoomType(reservation.getRoomType());
            reservationToUpdate.setCheckInDate(reservation.getCheckInDate());
            reservationToUpdate.setCheckOutDate(reservation.getCheckOutDate());
            reservationToUpdate.setNumberOfGuests(reservation.getNumberOfGuests());
            reservationToUpdate.setTotalCost(reservation.getTotalCost());
            reservationToUpdate.setStatus(reservation.getStatus());
            reservationToUpdate.setUpdatedAt(LocalDateTime.now());
            return reservationRepository.save(reservationToUpdate);
        } else {
            throw new RuntimeException("Reservation not found with id: " + reservationId);
        }
    }
    
    @Override
    public Reservation cancelReservation(Long reservationId) {
        Optional<Reservation> existingReservation = reservationRepository.findById(reservationId);
        if (existingReservation.isPresent()) {
            Reservation reservation = existingReservation.get();
            
            // Calculate refund amount based on cancellation policy
            double refundAmount = calculateRefundAmount(reservation);
            
            // Restore room availability when canceling reservation
            try {
                boolean updateSuccess = hotelServiceClient.updateRoomAvailability(
                    reservation.getHotelId(),
                    reservation.getRoomType(),
                    reservation.getNumberOfRooms(),
                    false // isReservation = false (canceling a reservation)
                );
                
                if (!updateSuccess) {
                    System.err.println("Warning: Failed to restore room availability for canceled reservation " + reservationId);
                }
            } catch (Exception e) {
                System.err.println("Error restoring room availability: " + e.getMessage());
            }
            
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservation.setRefundedAmount(refundAmount);
            reservation.setUpdatedAt(LocalDateTime.now());
            return reservationRepository.save(reservation);
        } else {
            throw new RuntimeException("Reservation not found with id: " + reservationId);
        }
    }
    
    private double calculateRefundAmount(Reservation reservation) {
        LocalDate today = LocalDate.now();
        LocalDate checkInDate = reservation.getCheckInDate();
        long daysUntilCheckIn = ChronoUnit.DAYS.between(today, checkInDate);
        
        double totalCost = reservation.getTotalCost();
        double refundPercentage = 0.0;
        
        if (daysUntilCheckIn >= 7) {
            refundPercentage = 1.0; // 100% refund
        } else if (daysUntilCheckIn >= 3) {
            refundPercentage = 0.75; // 75% refund
        } else if (daysUntilCheckIn >= 1) {
            refundPercentage = 0.5; // 50% refund
        } else {
            refundPercentage = 0.0; // 0% refund
        }
        
        return totalCost * refundPercentage;
    }
    
    @Override
    public void deleteReservation(Long reservationId) {
        if (reservationRepository.existsById(reservationId)) {
            reservationRepository.deleteById(reservationId);
        } else {
            throw new RuntimeException("Reservation not found with id: " + reservationId);
        }
    }
    
    @Override
    public List<Reservation> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }
    
    @Override
    public Reservation confirmReservation(Long reservationId) {
        Optional<Reservation> existingReservation = reservationRepository.findById(reservationId);
        if (existingReservation.isPresent()) {
            Reservation reservation = existingReservation.get();
            if (reservation.getStatus() == ReservationStatus.PENDING) {
                reservation.setStatus(ReservationStatus.CONFIRMED);
                reservation.setUpdatedAt(LocalDateTime.now());
                return reservationRepository.save(reservation);
            } else {
                throw new RuntimeException("Cannot confirm reservation with status: " + reservation.getStatus());
            }
        }
        throw new RuntimeException("Reservation not found with id: " + reservationId);
    }
    
    @Override
    public void deleteReservationsByHotelId(Long hotelId) {
        List<Reservation> reservations = reservationRepository.findByHotelId(hotelId);
        
        if (reservations.isEmpty()) {
            System.out.println("No reservations found for Hotel ID: " + hotelId);
            return;
        }
        
        System.out.println("Found " + reservations.size() + " reservations for Hotel ID: " + hotelId);
        
        // Restore room availability for each reservation before deleting
        for (Reservation reservation : reservations) {
            try {
                // Only restore availability if reservation was confirmed or pending
                if (reservation.getStatus() == ReservationStatus.CONFIRMED || 
                    reservation.getStatus() == ReservationStatus.PENDING) {
                    
                    boolean updateSuccess = hotelServiceClient.updateRoomAvailability(
                        reservation.getHotelId(),
                        reservation.getRoomType(),
                        reservation.getNumberOfRooms(),
                        false // isReservation = false (canceling/deleting reservation)
                    );
                    
                    if (!updateSuccess) {
                        System.err.println("Warning: Failed to restore room availability for reservation " + 
                                         reservation.getReservationId());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error restoring room availability for reservation " + 
                                 reservation.getReservationId() + ": " + e.getMessage());
            }
        }
        
        // Delete all reservations for the hotel
        reservationRepository.deleteAll(reservations);
        System.out.println("Successfully deleted " + reservations.size() + " reservations for Hotel ID: " + hotelId);
    }
}
