package com.code.reservationservice.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AvailabilityCheckResponse DTO.
 */
class AvailabilityCheckResponseTest {

    @Test
    @DisplayName("Should create response using builder")
    void shouldCreateResponseUsingBuilder() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        AvailabilityCheckResponse response = AvailabilityCheckResponse.builder()
                .riadId(100L)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .available(true)
                .message("Riad is available for the selected dates")
                .build();

        assertThat(response.getRiadId()).isEqualTo(100L);
        assertThat(response.getCheckInDate()).isEqualTo(checkIn);
        assertThat(response.getCheckOutDate()).isEqualTo(checkOut);
        assertThat(response.isAvailable()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Riad is available for the selected dates");
    }

    @Test
    @DisplayName("Should create unavailable response")
    void shouldCreateUnavailableResponse() {
        AvailabilityCheckResponse response = AvailabilityCheckResponse.builder()
                .riadId(100L)
                .available(false)
                .message("Riad is not available for the selected dates")
                .build();

        assertThat(response.isAvailable()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Riad is not available for the selected dates");
    }

    @Test
    @DisplayName("Should create empty response using no-args constructor")
    void shouldCreateEmptyResponse() {
        AvailabilityCheckResponse response = new AvailabilityCheckResponse();

        assertThat(response).isNotNull();
        assertThat(response.getRiadId()).isNull();
        assertThat(response.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("Should create response using all-args constructor")
    void shouldCreateResponseUsingAllArgsConstructor() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        AvailabilityCheckResponse response = new AvailabilityCheckResponse(
                100L, checkIn, checkOut, true, "Available"
        );

        assertThat(response.getRiadId()).isEqualTo(100L);
        assertThat(response.getCheckInDate()).isEqualTo(checkIn);
        assertThat(response.getCheckOutDate()).isEqualTo(checkOut);
        assertThat(response.isAvailable()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Available");
    }

    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        AvailabilityCheckResponse response = new AvailabilityCheckResponse();
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(5);

        response.setRiadId(200L);
        response.setCheckInDate(checkIn);
        response.setCheckOutDate(checkOut);
        response.setAvailable(true);
        response.setMessage("Test message");

        assertThat(response.getRiadId()).isEqualTo(200L);
        assertThat(response.getCheckInDate()).isEqualTo(checkIn);
        assertThat(response.getCheckOutDate()).isEqualTo(checkOut);
        assertThat(response.isAvailable()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Test message");
    }

    @Test
    @DisplayName("Should support equals and hashCode")
    void shouldSupportEqualsAndHashCode() {
        AvailabilityCheckResponse response1 = AvailabilityCheckResponse.builder()
                .riadId(100L)
                .available(true)
                .build();

        AvailabilityCheckResponse response2 = AvailabilityCheckResponse.builder()
                .riadId(100L)
                .available(true)
                .build();

        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("Should support toString")
    void shouldSupportToString() {
        AvailabilityCheckResponse response = AvailabilityCheckResponse.builder()
                .riadId(100L)
                .available(true)
                .build();

        String toString = response.toString();

        assertThat(toString).contains("riadId=100");
        assertThat(toString).contains("available=true");
    }
}

