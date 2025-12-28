package com.code.reservationservice.dto;

import com.code.reservationservice.dao.entity.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ReservationSearchCriteria DTO.
 */
class ReservationSearchCriteriaTest {

    @Test
    @DisplayName("Should create search criteria using builder with all fields")
    void shouldCreateSearchCriteriaUsingBuilder() {
        LocalDate checkInFrom = LocalDate.now();
        LocalDate checkInTo = LocalDate.now().plusDays(7);
        LocalDate checkOutFrom = LocalDate.now().plusDays(1);
        LocalDate checkOutTo = LocalDate.now().plusDays(8);

        ReservationSearchCriteria criteria = ReservationSearchCriteria.builder()
                .userId(1L)
                .riadId(100L)
                .status(ReservationStatus.CONFIRMED)
                .checkInDateFrom(checkInFrom)
                .checkInDateTo(checkInTo)
                .checkOutDateFrom(checkOutFrom)
                .checkOutDateTo(checkOutTo)
                .guestName("John")
                .reservationNumber("RES-12345678")
                .build();

        assertThat(criteria.getUserId()).isEqualTo(1L);
        assertThat(criteria.getRiadId()).isEqualTo(100L);
        assertThat(criteria.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(criteria.getCheckInDateFrom()).isEqualTo(checkInFrom);
        assertThat(criteria.getCheckInDateTo()).isEqualTo(checkInTo);
        assertThat(criteria.getCheckOutDateFrom()).isEqualTo(checkOutFrom);
        assertThat(criteria.getCheckOutDateTo()).isEqualTo(checkOutTo);
        assertThat(criteria.getGuestName()).isEqualTo("John");
        assertThat(criteria.getReservationNumber()).isEqualTo("RES-12345678");
    }

    @Test
    @DisplayName("Should create empty search criteria")
    void shouldCreateEmptySearchCriteria() {
        ReservationSearchCriteria criteria = ReservationSearchCriteria.builder().build();

        assertThat(criteria.getUserId()).isNull();
        assertThat(criteria.getRiadId()).isNull();
        assertThat(criteria.getStatus()).isNull();
        assertThat(criteria.getCheckInDateFrom()).isNull();
        assertThat(criteria.getCheckInDateTo()).isNull();
        assertThat(criteria.getCheckOutDateFrom()).isNull();
        assertThat(criteria.getCheckOutDateTo()).isNull();
        assertThat(criteria.getGuestName()).isNull();
        assertThat(criteria.getReservationNumber()).isNull();
    }

    @Test
    @DisplayName("Should create criteria with partial fields")
    void shouldCreateCriteriaWithPartialFields() {
        ReservationSearchCriteria criteria = ReservationSearchCriteria.builder()
                .userId(1L)
                .status(ReservationStatus.PENDING)
                .build();

        assertThat(criteria.getUserId()).isEqualTo(1L);
        assertThat(criteria.getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(criteria.getRiadId()).isNull();
    }

    @Test
    @DisplayName("Should create criteria using no-args constructor")
    void shouldCreateCriteriaUsingNoArgsConstructor() {
        ReservationSearchCriteria criteria = new ReservationSearchCriteria();

        assertThat(criteria).isNotNull();
        assertThat(criteria.getUserId()).isNull();
    }

    @Test
    @DisplayName("Should create criteria using all-args constructor")
    void shouldCreateCriteriaUsingAllArgsConstructor() {
        LocalDate checkInFrom = LocalDate.now();
        LocalDate checkInTo = LocalDate.now().plusDays(7);

        ReservationSearchCriteria criteria = new ReservationSearchCriteria(
                1L, 100L, ReservationStatus.CONFIRMED,
                checkInFrom, checkInTo, null, null,
                "John", "RES-12345678"
        );

        assertThat(criteria.getUserId()).isEqualTo(1L);
        assertThat(criteria.getRiadId()).isEqualTo(100L);
        assertThat(criteria.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(criteria.getCheckInDateFrom()).isEqualTo(checkInFrom);
        assertThat(criteria.getCheckInDateTo()).isEqualTo(checkInTo);
        assertThat(criteria.getGuestName()).isEqualTo("John");
        assertThat(criteria.getReservationNumber()).isEqualTo("RES-12345678");
    }

    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        ReservationSearchCriteria criteria = new ReservationSearchCriteria();

        criteria.setUserId(2L);
        criteria.setRiadId(200L);
        criteria.setStatus(ReservationStatus.CANCELLED);
        criteria.setGuestName("Jane");
        criteria.setReservationNumber("RES-ABCD1234");

        assertThat(criteria.getUserId()).isEqualTo(2L);
        assertThat(criteria.getRiadId()).isEqualTo(200L);
        assertThat(criteria.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(criteria.getGuestName()).isEqualTo("Jane");
        assertThat(criteria.getReservationNumber()).isEqualTo("RES-ABCD1234");
    }

    @Test
    @DisplayName("Should support equals and hashCode")
    void shouldSupportEqualsAndHashCode() {
        ReservationSearchCriteria criteria1 = ReservationSearchCriteria.builder()
                .userId(1L)
                .riadId(100L)
                .build();

        ReservationSearchCriteria criteria2 = ReservationSearchCriteria.builder()
                .userId(1L)
                .riadId(100L)
                .build();

        assertThat(criteria1).isEqualTo(criteria2);
        assertThat(criteria1.hashCode()).isEqualTo(criteria2.hashCode());
    }

    @Test
    @DisplayName("Should support toString")
    void shouldSupportToString() {
        ReservationSearchCriteria criteria = ReservationSearchCriteria.builder()
                .userId(1L)
                .status(ReservationStatus.CONFIRMED)
                .build();

        String toString = criteria.toString();

        assertThat(toString).contains("userId=1");
        assertThat(toString).contains("status=CONFIRMED");
    }
}

