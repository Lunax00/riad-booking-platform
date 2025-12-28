package com.code.reservationservice.scheduler;

import com.code.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled tasks for reservation management.
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ReservationScheduler {

    private final ReservationService reservationService;

    @Value("${reservation.expiration.hours:24}")
    private int expirationHours;

    /**
     * Expire pending reservations that are older than configured hours.
     * Runs every hour.
     */
    @Scheduled(fixedRate = 3600 * 1000) // Every hour
    public void expirePendingReservations() {
        log.info("Running scheduled task: Expire pending reservations older than {} hours", expirationHours);
        int expiredCount = reservationService.expirePendingReservations(expirationHours);
        log.info("Expired {} pending reservations", expiredCount);
    }
}

