package com.code.reservationservice.exception;

/**
 * Exception thrown when a reservation is not found.
 */
public class ReservationNotFoundException extends RuntimeException {

    public ReservationNotFoundException(String message) {
        super(message);
    }

    public ReservationNotFoundException(Long id) {
        super("Reservation not found with id: " + id);
    }

    public ReservationNotFoundException(String field, String value) {
        super("Reservation not found with " + field + ": " + value);
    }
}


