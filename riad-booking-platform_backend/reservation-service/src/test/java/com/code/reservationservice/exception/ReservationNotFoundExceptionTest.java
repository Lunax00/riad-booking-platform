package com.code.reservationservice.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ReservationNotFoundException.
 */
class ReservationNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void shouldCreateExceptionWithMessage() {
        ReservationNotFoundException exception = new ReservationNotFoundException("Custom message");

        assertThat(exception.getMessage()).isEqualTo("Custom message");
    }

    @Test
    @DisplayName("Should create exception with ID")
    void shouldCreateExceptionWithId() {
        ReservationNotFoundException exception = new ReservationNotFoundException(123L);

        assertThat(exception.getMessage()).isEqualTo("Reservation not found with id: 123");
    }

    @Test
    @DisplayName("Should create exception with field and value")
    void shouldCreateExceptionWithFieldAndValue() {
        ReservationNotFoundException exception = new ReservationNotFoundException("reservationNumber", "RES-12345678");

        assertThat(exception.getMessage()).isEqualTo("Reservation not found with reservationNumber: RES-12345678");
    }

    @Test
    @DisplayName("Should be a RuntimeException")
    void shouldBeRuntimeException() {
        ReservationNotFoundException exception = new ReservationNotFoundException("test");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}

