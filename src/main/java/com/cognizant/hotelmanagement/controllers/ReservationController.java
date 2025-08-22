package com.cognizant.hotelmanagement.controllers;

import com.cognizant.hotelmanagement.model.dao.services.ReservationService;
import com.cognizant.hotelmanagement.model.dao.services.UserService;
import com.cognizant.hotelmanagement.model.dao.services.HotelService;
import com.cognizant.hotelmanagement.model.pojo.Reservation;
import com.cognizant.hotelmanagement.model.pojo.User;
import com.cognizant.hotelmanagement.model.pojo.Hotel;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private HotelService hotelService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationRequest reservationRequest) {
        try {
            Optional<User> user = userService.getUserById(reservationRequest.getUserId());
            Optional<Hotel> hotel = hotelService.getHotelById(reservationRequest.getHotelId());
            
            if (!user.isPresent()) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            if (!hotel.isPresent()) {
                return new ResponseEntity<>("Hotel not found", HttpStatus.NOT_FOUND);
            }
            
            Reservation reservation = new Reservation(
                user.get(), hotel.get(),
                reservationRequest.getCheckInDate(),
                reservationRequest.getCheckOutDate(),
                reservationRequest.getRoomType(),
                reservationRequest.getNumberOfRooms(),
                reservationRequest.getNumberOfGuests(),
                0.0 // Total cost will be calculated by service
            );
            
            Reservation createdReservation = reservationService.createReservation(reservation);
            return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/{reservationId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<?> getReservationById(@PathVariable Long reservationId) {
        Optional<Reservation> reservation = reservationService.getReservationById(reservationId);
        if (reservation.isPresent()) {
            return new ResponseEntity<>(reservation.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Reservation not found", HttpStatus.NOT_FOUND);
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<Reservation>> getReservationsByUserId(@PathVariable Long userId) {
        List<Reservation> reservations = reservationService.getReservationsByUserId(userId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    
    @GetMapping("/hotel/{hotelId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Reservation>> getReservationsByHotelId(@PathVariable Long hotelId) {
        List<Reservation> reservations = reservationService.getReservationsByHotelId(hotelId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    
    @PutMapping("/{reservationId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<?> updateReservation(@PathVariable Long reservationId, 
                                             @Valid @RequestBody ReservationUpdateRequest updateRequest) {
        try {
            Optional<Reservation> existingReservation = reservationService.getReservationById(reservationId);
            if (!existingReservation.isPresent()) {
                return new ResponseEntity<>("Reservation not found", HttpStatus.NOT_FOUND);
            }
            
            Reservation reservation = existingReservation.get();
            reservation.setCheckInDate(updateRequest.getCheckInDate());
            reservation.setCheckOutDate(updateRequest.getCheckOutDate());
            reservation.setRoomType(updateRequest.getRoomType());
            reservation.setNumberOfRooms(updateRequest.getNumberOfRooms());
            reservation.setNumberOfGuests(updateRequest.getNumberOfGuests());
            
            Reservation updatedReservation = reservationService.updateReservation(reservationId, reservation);
            return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("/{reservationId}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<?> cancelReservation(@PathVariable Long reservationId) {
        try {
            reservationService.cancelReservation(reservationId);
            return new ResponseEntity<>("Reservation cancelled successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/{reservationId}/refund")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<?> calculateRefund(@PathVariable Long reservationId) {
        try {
            Double refundAmount = reservationService.calculateRefundAmount(reservationId);
            return new ResponseEntity<>("Refund amount: " + refundAmount, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    // Inner classes for request DTOs
    public static class ReservationRequest {
        private Long userId;
        private Long hotelId;
        private java.time.LocalDate checkInDate;
        private java.time.LocalDate checkOutDate;
        private Reservation.RoomType roomType;
        private Integer numberOfRooms;
        private Integer numberOfGuests;
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public Long getHotelId() { return hotelId; }
        public void setHotelId(Long hotelId) { this.hotelId = hotelId; }
        
        public java.time.LocalDate getCheckInDate() { return checkInDate; }
        public void setCheckInDate(java.time.LocalDate checkInDate) { this.checkInDate = checkInDate; }
        
        public java.time.LocalDate getCheckOutDate() { return checkOutDate; }
        public void setCheckOutDate(java.time.LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
        
        public Reservation.RoomType getRoomType() { return roomType; }
        public void setRoomType(Reservation.RoomType roomType) { this.roomType = roomType; }
        
        public Integer getNumberOfRooms() { return numberOfRooms; }
        public void setNumberOfRooms(Integer numberOfRooms) { this.numberOfRooms = numberOfRooms; }
        
        public Integer getNumberOfGuests() { return numberOfGuests; }
        public void setNumberOfGuests(Integer numberOfGuests) { this.numberOfGuests = numberOfGuests; }
    }
    
    public static class ReservationUpdateRequest {
        private java.time.LocalDate checkInDate;
        private java.time.LocalDate checkOutDate;
        private Reservation.RoomType roomType;
        private Integer numberOfRooms;
        private Integer numberOfGuests;
        
        // Getters and Setters
        public java.time.LocalDate getCheckInDate() { return checkInDate; }
        public void setCheckInDate(java.time.LocalDate checkInDate) { this.checkInDate = checkInDate; }
        
        public java.time.LocalDate getCheckOutDate() { return checkOutDate; }
        public void setCheckOutDate(java.time.LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
        
        public Reservation.RoomType getRoomType() { return roomType; }
        public void setRoomType(Reservation.RoomType roomType) { this.roomType = roomType; }
        
        public Integer getNumberOfRooms() { return numberOfRooms; }
        public void setNumberOfRooms(Integer numberOfRooms) { this.numberOfRooms = numberOfRooms; }
        
        public Integer getNumberOfGuests() { return numberOfGuests; }
        public void setNumberOfGuests(Integer numberOfGuests) { this.numberOfGuests = numberOfGuests; }
    }
}