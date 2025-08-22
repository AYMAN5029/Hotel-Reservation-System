package com.cognizant.hotelmanagement.model.exceptions;

public class ReservationConflictException extends RuntimeException {
    public ReservationConflictException(String message) {
        super(message);
    }
    
    public ReservationConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
