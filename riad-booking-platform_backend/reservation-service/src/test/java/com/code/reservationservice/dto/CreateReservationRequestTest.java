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
 * Unit tests for CreateReservationRequest DTO validation.
 */
class CreateReservationRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private CreateReservationRequest.CreateReservationRequestBuilder validRequestBuilder() {
        return CreateReservationRequest.builder()
                .userId(1L)
                .riadId(100L)
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(3))
                .numberOfGuests(2)
                .numberOfRooms(1)
                .totalPrice(new BigDecimal("500.00"))
                .guestName("John Doe")
                .guestEmail("john@example.com");
    }

    @Nested
    @DisplayName("Valid Request Tests")
    class ValidRequestTests {

        @Test
        @DisplayName("Should pass validation for valid request")
        void shouldPassValidationForValidRequest() {
            CreateReservationRequest request = validRequestBuilder().build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should allow setting currency to MAD explicitly")
        void shouldAllowSettingCurrencyExplicitly() {
            CreateReservationRequest request = validRequestBuilder().currency("MAD").build();

            assertThat(request.getCurrency()).isEqualTo("MAD");
        }
    }

    @Nested
    @DisplayName("User ID Validation")
    class UserIdValidation {

        @Test
        @DisplayName("Should fail when userId is null")
        void shouldFailWhenUserIdIsNull() {
            CreateReservationRequest request = validRequestBuilder().userId(null).build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("User ID is required");
        }
    }

    @Nested
    @DisplayName("Riad ID Validation")
    class RiadIdValidation {

        @Test
        @DisplayName("Should fail when riadId is null")
        void shouldFailWhenRiadIdIsNull() {
            CreateReservationRequest request = validRequestBuilder().riadId(null).build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Riad ID is required");
        }
    }

    @Nested
    @DisplayName("Check-in Date Validation")
    class CheckInDateValidation {

        @Test
        @DisplayName("Should fail when checkInDate is null")
        void shouldFailWhenCheckInDateIsNull() {
            CreateReservationRequest request = validRequestBuilder().checkInDate(null).build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Check-in date is required");
        }

        @Test
        @DisplayName("Should fail when checkInDate is in the past")
        void shouldFailWhenCheckInDateIsInThePast() {
            CreateReservationRequest request = validRequestBuilder()
                    .checkInDate(LocalDate.now().minusDays(1))
                    .build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Check-in date must be today or in the future");
        }

        @Test
        @DisplayName("Should pass when checkInDate is today")
        void shouldPassWhenCheckInDateIsToday() {
            CreateReservationRequest request = validRequestBuilder()
                    .checkInDate(LocalDate.now())
                    .build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            // Only checkOutDate validation might fail if it's not in the future
            assertThat(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("checkInDate"))).isTrue();
        }
    }

    @Nested
    @DisplayName("Check-out Date Validation")
    class CheckOutDateValidation {

        @Test
        @DisplayName("Should fail when checkOutDate is null")
        void shouldFailWhenCheckOutDateIsNull() {
            CreateReservationRequest request = validRequestBuilder().checkOutDate(null).build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Check-out date is required");
        }

        @Test
        @DisplayName("Should fail when checkOutDate is today")
        void shouldFailWhenCheckOutDateIsToday() {
            CreateReservationRequest request = validRequestBuilder()
                    .checkOutDate(LocalDate.now())
                    .build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Check-out date must be in the future");
        }
    }

    @Nested
    @DisplayName("Number of Guests Validation")
    class NumberOfGuestsValidation {

        @Test
        @DisplayName("Should fail when numberOfGuests is null")
        void shouldFailWhenNumberOfGuestsIsNull() {
            CreateReservationRequest request = validRequestBuilder().numberOfGuests(null).build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Number of guests is required");
        }

        @Test
        @DisplayName("Should fail when numberOfGuests is less than 1")
        void shouldFailWhenNumberOfGuestsIsLessThan1() {
            CreateReservationRequest request = validRequestBuilder().numberOfGuests(0).build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("At least 1 guest is required");
        }

        @Test
        @DisplayName("Should fail when numberOfGuests exceeds 20")
        void shouldFailWhenNumberOfGuestsExceeds20() {
            CreateReservationRequest request = validRequestBuilder().numberOfGuests(21).build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Maximum 20 guests allowed");
        }
    }

    @Nested
    @DisplayName("Number of Rooms Validation")
    class NumberOfRoomsValidation {

        @Test
        @DisplayName("Should fail when numberOfRooms is null")
        void shouldFailWhenNumberOfRoomsIsNull() {
            CreateReservationRequest request = validRequestBuilder().numberOfRooms(null).build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Number of rooms is required");
        }

        @Test
        @DisplayName("Should fail when numberOfRooms is less than 1")
        void shouldFailWhenNumberOfRoomsIsLessThan1() {
            CreateReservationRequest request = validRequestBuilder().numberOfRooms(0).build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("At least 1 room is required");
        }

        @Test
        @DisplayName("Should fail when numberOfRooms exceeds 10")
        void shouldFailWhenNumberOfRoomsExceeds10() {
            CreateReservationRequest request = validRequestBuilder().numberOfRooms(11).build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Maximum 10 rooms allowed");
        }
    }

    @Nested
    @DisplayName("Total Price Validation")
    class TotalPriceValidation {

        @Test
        @DisplayName("Should fail when totalPrice is null")
        void shouldFailWhenTotalPriceIsNull() {
            CreateReservationRequest request = validRequestBuilder().totalPrice(null).build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Total price is required");
        }

        @Test
        @DisplayName("Should fail when totalPrice is zero")
        void shouldFailWhenTotalPriceIsZero() {
            CreateReservationRequest request = validRequestBuilder().totalPrice(BigDecimal.ZERO).build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Total price must be greater than 0");
        }
    }

    @Nested
    @DisplayName("Guest Name Validation")
    class GuestNameValidation {

        @Test
        @DisplayName("Should fail when guestName is null")
        void shouldFailWhenGuestNameIsNull() {
            CreateReservationRequest request = validRequestBuilder().guestName(null).build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("Should fail when guestName is blank")
        void shouldFailWhenGuestNameIsBlank() {
            CreateReservationRequest request = validRequestBuilder().guestName("   ").build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Guest Email Validation")
    class GuestEmailValidation {

        @Test
        @DisplayName("Should fail when guestEmail is null")
        void shouldFailWhenGuestEmailIsNull() {
            CreateReservationRequest request = validRequestBuilder().guestEmail(null).build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("Should fail when guestEmail is invalid")
        void shouldFailWhenGuestEmailIsInvalid() {
            CreateReservationRequest request = validRequestBuilder().guestEmail("invalid-email").build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Invalid email format");
        }
    }

    @Nested
    @DisplayName("Special Requests Validation")
    class SpecialRequestsValidation {

        @Test
        @DisplayName("Should fail when specialRequests exceeds 500 characters")
        void shouldFailWhenSpecialRequestsExceeds500Characters() {
            String longText = "a".repeat(501);
            CreateReservationRequest request = validRequestBuilder().specialRequests(longText).build();

            Set<ConstraintViolation<CreateReservationRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Special requests cannot exceed 500 characters");
        }
    }
}

