package com.code.reservationservice.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for InvalidReservationOperationException.
 */
class InvalidReservationOperationExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void shouldCreateExceptionWithMessage() {
        InvalidReservationOperationException exception =
            new InvalidReservationOperationException("Cannot cancel a checked-out reservation");

        assertThat(exception.getMessage()).isEqualTo("Cannot cancel a checked-out reservation");
    }

    @Test
    @DisplayName("Should be a RuntimeException")
    void shouldBeRuntimeException() {
        InvalidReservationOperationException exception =
            new InvalidReservationOperationException("test");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should handle null message")
    void shouldHandleNullMessage() {
        InvalidReservationOperationException exception =
            new InvalidReservationOperationException(null);

        assertThat(exception.getMessage()).isNull();
    }
}

