package com.code.reservationservice.dao.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ReservationStatus enum.
 */
class ReservationStatusTest {

    @Test
    @DisplayName("Should have all expected status values")
    void shouldHaveAllExpectedStatusValues() {
        ReservationStatus[] statuses = ReservationStatus.values();

        assertThat(statuses).hasSize(7);
        assertThat(statuses).containsExactlyInAnyOrder(
                ReservationStatus.PENDING,
                ReservationStatus.CONFIRMED,
                ReservationStatus.CANCELLED,
                ReservationStatus.CHECKED_IN,
                ReservationStatus.CHECKED_OUT,
                ReservationStatus.NO_SHOW,
                ReservationStatus.EXPIRED
        );
    }

    @Test
    @DisplayName("Should return correct value from valueOf")
    void shouldReturnCorrectValueFromValueOf() {
        assertThat(ReservationStatus.valueOf("PENDING")).isEqualTo(ReservationStatus.PENDING);
        assertThat(ReservationStatus.valueOf("CONFIRMED")).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(ReservationStatus.valueOf("CANCELLED")).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(ReservationStatus.valueOf("CHECKED_IN")).isEqualTo(ReservationStatus.CHECKED_IN);
        assertThat(ReservationStatus.valueOf("CHECKED_OUT")).isEqualTo(ReservationStatus.CHECKED_OUT);
        assertThat(ReservationStatus.valueOf("NO_SHOW")).isEqualTo(ReservationStatus.NO_SHOW);
        assertThat(ReservationStatus.valueOf("EXPIRED")).isEqualTo(ReservationStatus.EXPIRED);
    }

    @Test
    @DisplayName("Should have correct ordinal values")
    void shouldHaveCorrectOrdinalValues() {
        assertThat(ReservationStatus.PENDING.ordinal()).isEqualTo(0);
        assertThat(ReservationStatus.CONFIRMED.ordinal()).isEqualTo(1);
        assertThat(ReservationStatus.CANCELLED.ordinal()).isEqualTo(2);
        assertThat(ReservationStatus.CHECKED_IN.ordinal()).isEqualTo(3);
        assertThat(ReservationStatus.CHECKED_OUT.ordinal()).isEqualTo(4);
        assertThat(ReservationStatus.NO_SHOW.ordinal()).isEqualTo(5);
        assertThat(ReservationStatus.EXPIRED.ordinal()).isEqualTo(6);
    }

    @Test
    @DisplayName("Should return correct name")
    void shouldReturnCorrectName() {
        assertThat(ReservationStatus.PENDING.name()).isEqualTo("PENDING");
        assertThat(ReservationStatus.CONFIRMED.name()).isEqualTo("CONFIRMED");
        assertThat(ReservationStatus.CANCELLED.name()).isEqualTo("CANCELLED");
        assertThat(ReservationStatus.CHECKED_IN.name()).isEqualTo("CHECKED_IN");
        assertThat(ReservationStatus.CHECKED_OUT.name()).isEqualTo("CHECKED_OUT");
        assertThat(ReservationStatus.NO_SHOW.name()).isEqualTo("NO_SHOW");
        assertThat(ReservationStatus.EXPIRED.name()).isEqualTo("EXPIRED");
    }
}

