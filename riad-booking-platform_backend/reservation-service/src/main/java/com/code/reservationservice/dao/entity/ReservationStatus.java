package com.code.reservationservice.dao.entity;

/**
 * Enum representing the different statuses a reservation can have.
 */
public enum ReservationStatus {
    PENDING,        // Reservation created, awaiting confirmation
    CONFIRMED,      // Reservation confirmed
    CANCELLED,      // Reservation cancelled by user or system
    CHECKED_IN,     // Guest has checked in
    CHECKED_OUT,    // Guest has checked out
    NO_SHOW,        // Guest did not show up
    EXPIRED         // Reservation expired (not confirmed in time)
}
