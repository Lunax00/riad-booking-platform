package com.code.reservationservice.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ErrorResponse DTO.
 */
class ErrorResponseTest {

    @Test
    @DisplayName("Should create error response using builder")
    void shouldCreateErrorResponseUsingBuilder() {
        LocalDateTime timestamp = LocalDateTime.now();

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(timestamp)
                .status(404)
                .error("Not Found")
                .message("Reservation not found")
                .path("/api/v1/reservations/123")
                .build();

        assertThat(response.getTimestamp()).isEqualTo(timestamp);
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getError()).isEqualTo("Not Found");
        assertThat(response.getMessage()).isEqualTo("Reservation not found");
        assertThat(response.getPath()).isEqualTo("/api/v1/reservations/123");
        assertThat(response.getValidationErrors()).isNull();
    }

    @Test
    @DisplayName("Should create error response with validation errors")
    void shouldCreateErrorResponseWithValidationErrors() {
        Map<String, String> validationErrors = new HashMap<>();
        validationErrors.put("guestEmail", "Invalid email format");
        validationErrors.put("numberOfGuests", "At least 1 guest is required");

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(400)
                .error("Validation Failed")
                .message("One or more fields have validation errors")
                .path("/api/v1/reservations")
                .validationErrors(validationErrors)
                .build();

        assertThat(response.getValidationErrors()).hasSize(2);
        assertThat(response.getValidationErrors()).containsEntry("guestEmail", "Invalid email format");
        assertThat(response.getValidationErrors()).containsEntry("numberOfGuests", "At least 1 guest is required");
    }

    @Test
    @DisplayName("Should create empty error response using no-args constructor")
    void shouldCreateEmptyErrorResponse() {
        ErrorResponse response = new ErrorResponse();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(0);
        assertThat(response.getError()).isNull();
    }

    @Test
    @DisplayName("Should create error response using all-args constructor")
    void shouldCreateErrorResponseUsingAllArgsConstructor() {
        LocalDateTime timestamp = LocalDateTime.now();
        Map<String, String> validationErrors = new HashMap<>();

        ErrorResponse response = new ErrorResponse(
                timestamp, 500, "Internal Server Error",
                "An unexpected error occurred", "/api/v1/reservations",
                validationErrors
        );

        assertThat(response.getTimestamp()).isEqualTo(timestamp);
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getError()).isEqualTo("Internal Server Error");
    }

    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        ErrorResponse response = new ErrorResponse();
        LocalDateTime timestamp = LocalDateTime.now();
        Map<String, String> errors = new HashMap<>();
        errors.put("field", "error");

        response.setTimestamp(timestamp);
        response.setStatus(409);
        response.setError("Conflict");
        response.setMessage("Riad not available");
        response.setPath("/api/v1/reservations");
        response.setValidationErrors(errors);

        assertThat(response.getTimestamp()).isEqualTo(timestamp);
        assertThat(response.getStatus()).isEqualTo(409);
        assertThat(response.getError()).isEqualTo("Conflict");
        assertThat(response.getMessage()).isEqualTo("Riad not available");
        assertThat(response.getPath()).isEqualTo("/api/v1/reservations");
        assertThat(response.getValidationErrors()).containsEntry("field", "error");
    }

    @Test
    @DisplayName("Should support equals and hashCode")
    void shouldSupportEqualsAndHashCode() {
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 1, 12, 0);

        ErrorResponse response1 = ErrorResponse.builder()
                .timestamp(timestamp)
                .status(404)
                .error("Not Found")
                .message("Test")
                .path("/test")
                .build();

        ErrorResponse response2 = ErrorResponse.builder()
                .timestamp(timestamp)
                .status(404)
                .error("Not Found")
                .message("Test")
                .path("/test")
                .build();

        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("Should support toString")
    void shouldSupportToString() {
        ErrorResponse response = ErrorResponse.builder()
                .status(404)
                .error("Not Found")
                .build();

        String toString = response.toString();

        assertThat(toString).contains("status=404");
        assertThat(toString).contains("error=Not Found");
    }
}

