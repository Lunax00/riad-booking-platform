package com.code.reservationservice.scheduler;

import com.code.reservationservice.service.ReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

/**
 * Unit tests for ReservationScheduler.
 */
@ExtendWith(MockitoExtension.class)
class ReservationSchedulerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationScheduler reservationScheduler;

    @Test
    @DisplayName("Should expire pending reservations")
    void shouldExpirePendingReservations() {
        // Set the expiration hours value
        ReflectionTestUtils.setField(reservationScheduler, "expirationHours", 24);

        when(reservationService.expirePendingReservations(24)).thenReturn(5);

        reservationScheduler.expirePendingReservations();

        verify(reservationService).expirePendingReservations(24);
    }

    @Test
    @DisplayName("Should handle zero expired reservations")
    void shouldHandleZeroExpiredReservations() {
        ReflectionTestUtils.setField(reservationScheduler, "expirationHours", 24);

        when(reservationService.expirePendingReservations(24)).thenReturn(0);

        reservationScheduler.expirePendingReservations();

        verify(reservationService).expirePendingReservations(24);
    }

    @Test
    @DisplayName("Should use configured expiration hours")
    void shouldUseConfiguredExpirationHours() {
        ReflectionTestUtils.setField(reservationScheduler, "expirationHours", 48);

        when(reservationService.expirePendingReservations(48)).thenReturn(3);

        reservationScheduler.expirePendingReservations();

        verify(reservationService).expirePendingReservations(48);
    }
}

