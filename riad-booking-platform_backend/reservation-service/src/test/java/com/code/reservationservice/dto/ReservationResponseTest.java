package com.code.reservationservice.dto;

import com.code.reservationservice.dao.entity.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ReservationResponse DTO.
 */
class ReservationResponseTest {

    @Test
    @DisplayName("Should create reservation response with all fields using builder")
    void shouldCreateReservationResponseWithBuilder() {
        LocalDateTime now = LocalDateTime.now();

        ReservationResponse response = ReservationResponse.builder()
                .id(1L)
                .reservationNumber("RES-12345678")
                .userId(1L)
                .riadId(100L)
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(3))
                .numberOfGuests(2)
                .numberOfRooms(1)
                .status(ReservationStatus.CONFIRMED)
                .totalPrice(new BigDecimal("500.00"))
                .depositAmount(new BigDecimal("100.00"))
                .currency("MAD")
                .specialRequests("Late check-in")
                .guestName("John Doe")
                .guestEmail("john@example.com")
                .guestPhone("+212600000000")
                .paymentId("PAY-123")
                .cancellationReason(null)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getReservationNumber()).isEqualTo("RES-12345678");
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getRiadId()).isEqualTo(100L);
        assertThat(response.getCheckInDate()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(response.getCheckOutDate()).isEqualTo(LocalDate.now().plusDays(3));
        assertThat(response.getNumberOfGuests()).isEqualTo(2);
        assertThat(response.getNumberOfRooms()).isEqualTo(1);
        assertThat(response.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(response.getTotalPrice()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(response.getDepositAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(response.getCurrency()).isEqualTo("MAD");
        assertThat(response.getSpecialRequests()).isEqualTo("Late check-in");
        assertThat(response.getGuestName()).isEqualTo("John Doe");
        assertThat(response.getGuestEmail()).isEqualTo("john@example.com");
        assertThat(response.getGuestPhone()).isEqualTo("+212600000000");
        assertThat(response.getPaymentId()).isEqualTo("PAY-123");
        assertThat(response.getCancellationReason()).isNull();
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should create empty reservation response using no-args constructor")
    void shouldCreateEmptyReservationResponse() {
        ReservationResponse response = new ReservationResponse();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNull();
        assertThat(response.getReservationNumber()).isNull();
    }

    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        ReservationResponse response = new ReservationResponse();

        response.setId(1L);
        response.setReservationNumber("RES-TEST1234");
        response.setUserId(2L);
        response.setRiadId(100L);
        response.setStatus(ReservationStatus.PENDING);
        response.setTotalPrice(new BigDecimal("750.00"));
        response.setCurrency("USD");
        response.setGuestName("Jane Doe");
        response.setGuestEmail("jane@example.com");
        response.setCancellationReason("Guest requested");

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getReservationNumber()).isEqualTo("RES-TEST1234");
        assertThat(response.getUserId()).isEqualTo(2L);
        assertThat(response.getRiadId()).isEqualTo(100L);
        assertThat(response.getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(response.getTotalPrice()).isEqualByComparingTo(new BigDecimal("750.00"));
        assertThat(response.getCurrency()).isEqualTo("USD");
        assertThat(response.getGuestName()).isEqualTo("Jane Doe");
        assertThat(response.getGuestEmail()).isEqualTo("jane@example.com");
        assertThat(response.getCancellationReason()).isEqualTo("Guest requested");
    }

    @Test
    @DisplayName("Should support equals and hashCode")
    void shouldSupportEqualsAndHashCode() {
        ReservationResponse response1 = ReservationResponse.builder()
                .id(1L)
                .reservationNumber("RES-12345678")
                .build();

        ReservationResponse response2 = ReservationResponse.builder()
                .id(1L)
                .reservationNumber("RES-12345678")
                .build();

        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("Should support toString")
    void shouldSupportToString() {
        ReservationResponse response = ReservationResponse.builder()
                .id(1L)
                .reservationNumber("RES-12345678")
                .build();

        String toString = response.toString();

        assertThat(toString).contains("id=1");
        assertThat(toString).contains("reservationNumber=RES-12345678");
    }
}

