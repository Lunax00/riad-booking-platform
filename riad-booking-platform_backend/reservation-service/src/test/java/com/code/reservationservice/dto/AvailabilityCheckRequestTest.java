package com.code.reservationservice.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AvailabilityCheckRequest DTO.
 */
class AvailabilityCheckRequestTest {

    @Test
    @DisplayName("Should create request using builder")
    void shouldCreateRequestUsingBuilder() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        AvailabilityCheckRequest request = AvailabilityCheckRequest.builder()
                .riadId(100L)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .build();

        assertThat(request.getRiadId()).isEqualTo(100L);
        assertThat(request.getCheckInDate()).isEqualTo(checkIn);
        assertThat(request.getCheckOutDate()).isEqualTo(checkOut);
    }

    @Test
    @DisplayName("Should create empty request using no-args constructor")
    void shouldCreateEmptyRequest() {
        AvailabilityCheckRequest request = new AvailabilityCheckRequest();

        assertThat(request).isNotNull();
        assertThat(request.getRiadId()).isNull();
        assertThat(request.getCheckInDate()).isNull();
        assertThat(request.getCheckOutDate()).isNull();
    }

    @Test
    @DisplayName("Should create request using all-args constructor")
    void shouldCreateRequestUsingAllArgsConstructor() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        AvailabilityCheckRequest request = new AvailabilityCheckRequest(100L, checkIn, checkOut);

        assertThat(request.getRiadId()).isEqualTo(100L);
        assertThat(request.getCheckInDate()).isEqualTo(checkIn);
        assertThat(request.getCheckOutDate()).isEqualTo(checkOut);
    }

    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        AvailabilityCheckRequest request = new AvailabilityCheckRequest();
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(5);

        request.setRiadId(200L);
        request.setCheckInDate(checkIn);
        request.setCheckOutDate(checkOut);

        assertThat(request.getRiadId()).isEqualTo(200L);
        assertThat(request.getCheckInDate()).isEqualTo(checkIn);
        assertThat(request.getCheckOutDate()).isEqualTo(checkOut);
    }

    @Test
    @DisplayName("Should support equals and hashCode")
    void shouldSupportEqualsAndHashCode() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        AvailabilityCheckRequest request1 = AvailabilityCheckRequest.builder()
                .riadId(100L)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .build();

        AvailabilityCheckRequest request2 = AvailabilityCheckRequest.builder()
                .riadId(100L)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .build();

        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    @DisplayName("Should support toString")
    void shouldSupportToString() {
        AvailabilityCheckRequest request = AvailabilityCheckRequest.builder()
                .riadId(100L)
                .build();

        String toString = request.toString();

        assertThat(toString).contains("riadId=100");
    }
}

