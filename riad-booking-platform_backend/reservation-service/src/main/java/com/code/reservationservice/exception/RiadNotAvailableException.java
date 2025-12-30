package com.code.reservationservice.exception;

/**
 * Exception thrown when a riad is not available for the requested dates.
 */
public class RiadNotAvailableException extends RuntimeException {

    public RiadNotAvailableException(String message) {
        super(message);
    }

    public RiadNotAvailableException(Long riadId) {
        super("Riad with id " + riadId + " is not available for the requested dates");
    }
}

