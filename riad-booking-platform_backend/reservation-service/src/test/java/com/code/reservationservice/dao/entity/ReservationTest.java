package com.code.reservationservice.dao.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Reservation entity.
 */
class ReservationTest {

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = Reservation.builder()
                .userId(1L)
                .riadId(100L)
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(3))
                .numberOfGuests(2)
                .numberOfRooms(1)
                .totalPrice(new BigDecimal("500.00"))
                .guestName("John Doe")
                .guestEmail("john@example.com")
                .guestPhone("+212600000000")
                .build();
    }

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should create reservation with all fields")
        void shouldCreateReservationWithAllFields() {
            assertThat(reservation.getUserId()).isEqualTo(1L);
            assertThat(reservation.getRiadId()).isEqualTo(100L);
            assertThat(reservation.getCheckInDate()).isEqualTo(LocalDate.now().plusDays(1));
            assertThat(reservation.getCheckOutDate()).isEqualTo(LocalDate.now().plusDays(3));
            assertThat(reservation.getNumberOfGuests()).isEqualTo(2);
            assertThat(reservation.getNumberOfRooms()).isEqualTo(1);
            assertThat(reservation.getTotalPrice()).isEqualByComparingTo(new BigDecimal("500.00"));
            assertThat(reservation.getGuestName()).isEqualTo("John Doe");
            assertThat(reservation.getGuestEmail()).isEqualTo("john@example.com");
            assertThat(reservation.getGuestPhone()).isEqualTo("+212600000000");
        }

        @Test
        @DisplayName("Should set default status to PENDING")
        void shouldSetDefaultStatusToPending() {
            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
        }

        @Test
        @DisplayName("Should set default currency to MAD")
        void shouldSetDefaultCurrencyToMAD() {
            assertThat(reservation.getCurrency()).isEqualTo("MAD");
        }
    }

    @Nested
    @DisplayName("Pre-Persist Tests")
    class PrePersistTests {

        @Test
        @DisplayName("Should generate reservation number on prePersist")
        void shouldGenerateReservationNumberOnPrePersist() {
            assertThat(reservation.getReservationNumber()).isNull();
            
            reservation.generateReservationNumber();
            
            assertThat(reservation.getReservationNumber()).isNotNull();
            assertThat(reservation.getReservationNumber()).startsWith("RES-");
            assertThat(reservation.getReservationNumber()).hasSize(12); // "RES-" + 8 chars
        }

        @Test
        @DisplayName("Should not regenerate reservation number if already set")
        void shouldNotRegenerateReservationNumberIfAlreadySet() {
            reservation.setReservationNumber("RES-EXISTING1");
            
            reservation.generateReservationNumber();
            
            assertThat(reservation.getReservationNumber()).isEqualTo("RES-EXISTING1");
        }
    }

    @Nested
    @DisplayName("Setter Tests")
    class SetterTests {

        @Test
        @DisplayName("Should update status")
        void shouldUpdateStatus() {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            
            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        }

        @Test
        @DisplayName("Should update cancellation reason")
        void shouldUpdateCancellationReason() {
            reservation.setCancellationReason("Guest requested cancellation");
            
            assertThat(reservation.getCancellationReason()).isEqualTo("Guest requested cancellation");
        }

        @Test
        @DisplayName("Should update payment ID")
        void shouldUpdatePaymentId() {
            reservation.setPaymentId("PAY-123456");
            
            assertThat(reservation.getPaymentId()).isEqualTo("PAY-123456");
        }

        @Test
        @DisplayName("Should update special requests")
        void shouldUpdateSpecialRequests() {
            reservation.setSpecialRequests("Late check-in requested");
            
            assertThat(reservation.getSpecialRequests()).isEqualTo("Late check-in requested");
        }

        @Test
        @DisplayName("Should update deposit amount")
        void shouldUpdateDepositAmount() {
            reservation.setDepositAmount(new BigDecimal("100.00"));
            
            assertThat(reservation.getDepositAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        }
    }

    @Nested
    @DisplayName("NoArgsConstructor Tests")
    class NoArgsConstructorTests {

        @Test
        @DisplayName("Should create empty reservation using no-args constructor")
        void shouldCreateEmptyReservation() {
            Reservation emptyReservation = new Reservation();
            
            assertThat(emptyReservation).isNotNull();
            assertThat(emptyReservation.getId()).isNull();
            assertThat(emptyReservation.getUserId()).isNull();
        }
    }

    @Nested
    @DisplayName("AllArgsConstructor Tests")
    class AllArgsConstructorTests {

        @Test
        @DisplayName("Should create reservation using all-args constructor")
        void shouldCreateReservationWithAllArgsConstructor() {
            Reservation fullReservation = new Reservation(
                    1L, "RES-12345678", 1L, 100L,
                    LocalDate.now().plusDays(1), LocalDate.now().plusDays(3),
                    2, 1, ReservationStatus.PENDING,
                    new BigDecimal("500.00"), new BigDecimal("100.00"), "MAD",
                    "No requests", "John Doe", "john@example.com", "+212600000000",
                    "PAY-123", null, null, null, null
            );
            
            assertThat(fullReservation.getId()).isEqualTo(1L);
            assertThat(fullReservation.getReservationNumber()).isEqualTo("RES-12345678");
        }
    }
}

