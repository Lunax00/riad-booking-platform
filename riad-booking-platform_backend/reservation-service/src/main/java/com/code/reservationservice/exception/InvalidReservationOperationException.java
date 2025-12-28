package com.code.reservationservice.exception;

/**
 * Exception thrown when an invalid reservation operation is attempted.
 */
public class InvalidReservationOperationException extends RuntimeException {

    public InvalidReservationOperationException(String message) {
        super(message);
    }
}

