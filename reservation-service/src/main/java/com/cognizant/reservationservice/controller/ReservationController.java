package com.cognizant.reservationservice.controller;

import com.cognizant.reservationservice.model.Reservation;
import com.cognizant.reservationservice.model.ReservationStatus;
import com.cognizant.reservationservice.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;
    
    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody Reservation reservation) {
        try {
            Reservation createdReservation = reservationService.createReservation(reservation);
            return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/{reservationId}")
    public ResponseEntity<?> getReservationById(@PathVariable Long reservationId) {
        Optional<Reservation> reservation = reservationService.getReservationById(reservationId);
        if (reservation.isPresent()) {
            return new ResponseEntity<>(reservation.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Reservation not found", HttpStatus.NOT_FOUND);
    }
    
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservation>> getReservationsByUserId(@PathVariable Long userId) {
        List<Reservation> reservations = reservationService.getReservationsByUserId(userId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Reservation>> getReservationsByHotelId(@PathVariable Long hotelId) {
        List<Reservation> reservations = reservationService.getReservationsByHotelId(hotelId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Reservation>> getReservationsByStatus(@PathVariable ReservationStatus status) {
        List<Reservation> reservations = reservationService.getReservationsByStatus(status);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    
    @PutMapping("/{reservationId}")
    public ResponseEntity<?> updateReservation(@PathVariable Long reservationId, @Valid @RequestBody Reservation reservation) {
        try {
            Reservation updatedReservation = reservationService.updateReservation(reservationId, reservation);
            return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("/{reservationId}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable Long reservationId) {
        try {
            Reservation cancelledReservation = reservationService.cancelReservation(reservationId);
            return new ResponseEntity<>(cancelledReservation, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long reservationId) {
        try {
            reservationService.deleteReservation(reservationId);
            return new ResponseEntity<>("Reservation deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/{reservationId}/confirm")
    public ResponseEntity<Reservation> confirmReservation(@PathVariable Long reservationId) {
        try {
            Reservation confirmedReservation = reservationService.confirmReservation(reservationId);
            return new ResponseEntity<>(confirmedReservation, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/hotel/{hotelId}")
    public ResponseEntity<?> deleteReservationsByHotelId(@PathVariable Long hotelId) {
        try {
            reservationService.deleteReservationsByHotelId(hotelId);
            return new ResponseEntity<>("All reservations for Hotel ID " + hotelId + " deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting reservations: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
