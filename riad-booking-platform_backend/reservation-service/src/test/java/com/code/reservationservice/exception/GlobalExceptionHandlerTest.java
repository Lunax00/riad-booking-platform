package com.code.reservationservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GlobalExceptionHandler.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/v1/reservations");
    }

    @Nested
    @DisplayName("ReservationNotFoundException Handler")
    class ReservationNotFoundExceptionHandler {

        @Test
        @DisplayName("Should return 404 Not Found response")
        void shouldReturn404NotFoundResponse() {
            ReservationNotFoundException exception = new ReservationNotFoundException(123L);

            ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleReservationNotFound(exception, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(404);
            assertThat(response.getBody().getError()).isEqualTo("Not Found");
            assertThat(response.getBody().getMessage()).isEqualTo("Reservation not found with id: 123");
            assertThat(response.getBody().getPath()).isEqualTo("/api/v1/reservations");
        }
    }

    @Nested
    @DisplayName("RiadNotAvailableException Handler")
    class RiadNotAvailableExceptionHandler {

        @Test
        @DisplayName("Should return 409 Conflict response")
        void shouldReturn409ConflictResponse() {
            RiadNotAvailableException exception = new RiadNotAvailableException(100L);

            ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleRiadNotAvailable(exception, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(409);
            assertThat(response.getBody().getError()).isEqualTo("Conflict");
            assertThat(response.getBody().getMessage()).contains("is not available");
        }
    }

    @Nested
    @DisplayName("InvalidReservationOperationException Handler")
    class InvalidReservationOperationExceptionHandler {

        @Test
        @DisplayName("Should return 400 Bad Request response")
        void shouldReturn400BadRequestResponse() {
            InvalidReservationOperationException exception =
                new InvalidReservationOperationException("Cannot confirm already confirmed reservation");

            ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleInvalidOperation(exception, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
            assertThat(response.getBody().getError()).isEqualTo("Bad Request");
            assertThat(response.getBody().getMessage()).isEqualTo("Cannot confirm already confirmed reservation");
        }
    }

    @Nested
    @DisplayName("MethodArgumentNotValidException Handler")
    class MethodArgumentNotValidExceptionHandler {

        @Test
        @DisplayName("Should return 400 Bad Request with validation errors")
        void shouldReturn400WithValidationErrors() {
            BindingResult bindingResult = mock(BindingResult.class);
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);

            FieldError fieldError1 = new FieldError("request", "guestEmail", "Invalid email format");
            FieldError fieldError2 = new FieldError("request", "numberOfGuests", "At least 1 guest is required");

            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

            ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleValidationErrors(exception, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
            assertThat(response.getBody().getError()).isEqualTo("Validation Failed");
            assertThat(response.getBody().getValidationErrors()).hasSize(2);
            assertThat(response.getBody().getValidationErrors()).containsEntry("guestEmail", "Invalid email format");
            assertThat(response.getBody().getValidationErrors()).containsEntry("numberOfGuests", "At least 1 guest is required");
        }
    }

    @Nested
    @DisplayName("IllegalArgumentException Handler")
    class IllegalArgumentExceptionHandler {

        @Test
        @DisplayName("Should return 400 Bad Request response")
        void shouldReturn400BadRequestResponse() {
            IllegalArgumentException exception = new IllegalArgumentException("Check-out date must be after check-in date");

            ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleIllegalArgument(exception, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
            assertThat(response.getBody().getError()).isEqualTo("Bad Request");
            assertThat(response.getBody().getMessage()).isEqualTo("Check-out date must be after check-in date");
        }
    }

    @Nested
    @DisplayName("Generic Exception Handler")
    class GenericExceptionHandler {

        @Test
        @DisplayName("Should return 500 Internal Server Error response")
        void shouldReturn500InternalServerErrorResponse() {
            Exception exception = new RuntimeException("Unexpected error");

            ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleGenericException(exception, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(500);
            assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
            assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
        }
    }
}

