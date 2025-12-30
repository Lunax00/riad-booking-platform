package com.code.reservationservice.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for RiadNotAvailableException.
 */
class RiadNotAvailableExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void shouldCreateExceptionWithMessage() {
        RiadNotAvailableException exception = new RiadNotAvailableException("Custom availability message");

        assertThat(exception.getMessage()).isEqualTo("Custom availability message");
    }

    @Test
    @DisplayName("Should create exception with riad ID")
    void shouldCreateExceptionWithRiadId() {
        RiadNotAvailableException exception = new RiadNotAvailableException(100L);

        assertThat(exception.getMessage()).isEqualTo("Riad with id 100 is not available for the requested dates");
    }

    @Test
    @DisplayName("Should be a RuntimeException")
    void shouldBeRuntimeException() {
        RiadNotAvailableException exception = new RiadNotAvailableException("test");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}

