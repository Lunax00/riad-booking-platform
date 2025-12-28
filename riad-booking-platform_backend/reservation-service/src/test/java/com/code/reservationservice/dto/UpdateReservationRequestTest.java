package com.code.reservationservice.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for UpdateReservationRequest DTO validation.
 */
class UpdateReservationRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Valid Request Tests")
    class ValidRequestTests {

        @Test
        @DisplayName("Should pass validation for empty request (all fields optional)")
        void shouldPassValidationForEmptyRequest() {
            UpdateReservationRequest request = UpdateReservationRequest.builder().build();

            Set<ConstraintViolation<UpdateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation for valid partial update")
        void shouldPassValidationForValidPartialUpdate() {
            UpdateReservationRequest request = UpdateReservationRequest.builder()
                    .numberOfGuests(4)
                    .guestName("Updated Name")
                    .build();

            Set<ConstraintViolation<UpdateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Check-in Date Validation")
    class CheckInDateValidation {

        @Test
        @DisplayName("Should fail when checkInDate is in the past")
        void shouldFailWhenCheckInDateIsInThePast() {
            UpdateReservationRequest request = UpdateReservationRequest.builder()
                    .checkInDate(LocalDate.now().minusDays(1))
                    .build();

            Set<ConstraintViolation<UpdateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Check-in date must be today or in the future");
        }

        @Test
        @DisplayName("Should pass when checkInDate is today")
        void shouldPassWhenCheckInDateIsToday() {
            UpdateReservationRequest request = UpdateReservationRequest.builder()
                    .checkInDate(LocalDate.now())
                    .build();

            Set<ConstraintViolation<UpdateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Check-out Date Validation")
    class CheckOutDateValidation {

        @Test
        @DisplayName("Should fail when checkOutDate is today")
        void shouldFailWhenCheckOutDateIsToday() {
            UpdateReservationRequest request = UpdateReservationRequest.builder()
                    .checkOutDate(LocalDate.now())
                    .build();

            Set<ConstraintViolation<UpdateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Check-out date must be in the future");
        }

        @Test
        @DisplayName("Should pass when checkOutDate is in the future")
        void shouldPassWhenCheckOutDateIsInTheFuture() {
            UpdateReservationRequest request = UpdateReservationRequest.builder()
                    .checkOutDate(LocalDate.now().plusDays(1))
                    .build();

            Set<ConstraintViolation<UpdateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Number of Guests Validation")
    class NumberOfGuestsValidation {

        @Test
        @DisplayName("Should fail when numberOfGuests is less than 1")
        void shouldFailWhenNumberOfGuestsIsLessThan1() {
            UpdateReservationRequest request = UpdateReservationRequest.builder()
                    .numberOfGuests(0)
                    .build();

            Set<ConstraintViolation<UpdateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("At least 1 guest is required");
        }

        @Test
        @DisplayName("Should fail when numberOfGuests exceeds 20")
        void shouldFailWhenNumberOfGuestsExceeds20() {
            UpdateReservationRequest request = UpdateReservationRequest.builder()
                    .numberOfGuests(21)
                    .build();

            Set<ConstraintViolation<UpdateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Maximum 20 guests allowed");
        }
    }

    @Nested
    @DisplayName("Number of Rooms Validation")
    class NumberOfRoomsValidation {

        @Test
        @DisplayName("Should fail when numberOfRooms is less than 1")
        void shouldFailWhenNumberOfRoomsIsLessThan1() {
            UpdateReservationRequest request = UpdateReservationRequest.builder()
                    .numberOfRooms(0)
                    .build();

            Set<ConstraintViolation<UpdateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("At least 1 room is required");
        }

        @Test
        @DisplayName("Should fail when numberOfRooms exceeds 10")
        void shouldFailWhenNumberOfRoomsExceeds10() {
            UpdateReservationRequest request = UpdateReservationRequest.builder()
                    .numberOfRooms(11)
                    .build();

            Set<ConstraintViolation<UpdateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Maximum 10 rooms allowed");
        }
    }

    @Nested
    @DisplayName("Total Price Validation")
    class TotalPriceValidation {

        @Test
        @DisplayName("Should fail when totalPrice is zero")
        void shouldFailWhenTotalPriceIsZero() {
            UpdateReservationRequest request = UpdateReservationRequest.builder()
                    .totalPrice(BigDecimal.ZERO)
                    .build();

            Set<ConstraintViolation<UpdateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Total price must be greater than 0");
        }

        @Test
        @DisplayName("Should pass when totalPrice is valid")
        void shouldPassWhenTotalPriceIsValid() {
            UpdateReservationRequest request = UpdateReservationRequest.builder()
                    .totalPrice(new BigDecimal("100.00"))
                    .build();

            Set<ConstraintViolation<UpdateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Guest Email Validation")
    class GuestEmailValidation {

        @Test
        @DisplayName("Should fail when guestEmail is invalid")
        void shouldFailWhenGuestEmailIsInvalid() {
            UpdateReservationRequest request = UpdateReservationRequest.builder()
                    .guestEmail("invalid-email")
                    .build();

            Set<ConstraintViolation<UpdateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Invalid email format");
        }

        @Test
        @DisplayName("Should pass when guestEmail is valid")
        void shouldPassWhenGuestEmailIsValid() {
            UpdateReservationRequest request = UpdateReservationRequest.builder()
                    .guestEmail("valid@example.com")
                    .build();

            Set<ConstraintViolation<UpdateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Special Requests Validation")
    class SpecialRequestsValidation {

        @Test
        @DisplayName("Should fail when specialRequests exceeds 500 characters")
        void shouldFailWhenSpecialRequestsExceeds500Characters() {
            String longText = "a".repeat(501);
            UpdateReservationRequest request = UpdateReservationRequest.builder()
                    .specialRequests(longText)
                    .build();

            Set<ConstraintViolation<UpdateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Special requests cannot exceed 500 characters");
        }
    }

    @Nested
    @DisplayName("Guest Name Validation")
    class GuestNameValidation {

        @Test
        @DisplayName("Should fail when guestName exceeds 100 characters")
        void shouldFailWhenGuestNameExceeds100Characters() {
            String longName = "a".repeat(101);
            UpdateReservationRequest request = UpdateReservationRequest.builder()
                    .guestName(longName)
                    .build();

            Set<ConstraintViolation<UpdateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Guest name cannot exceed 100 characters");
        }
    }

    @Nested
    @DisplayName("Guest Phone Validation")
    class GuestPhoneValidation {

        @Test
        @DisplayName("Should fail when guestPhone exceeds 20 characters")
        void shouldFailWhenGuestPhoneExceeds20Characters() {
            String longPhone = "1".repeat(21);
            UpdateReservationRequest request = UpdateReservationRequest.builder()
                    .guestPhone(longPhone)
                    .build();

            Set<ConstraintViolation<UpdateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Guest phone cannot exceed 20 characters");
        }
    }
}

