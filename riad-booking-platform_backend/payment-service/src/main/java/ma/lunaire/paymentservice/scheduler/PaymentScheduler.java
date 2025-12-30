package ma.lunaire.paymentservice.scheduler;

import ma.lunaire.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler for payment-related background tasks.
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class PaymentScheduler {

    private final PaymentService paymentService;

    /**
     * Expire pending payments every hour.
     * Payments that have been pending for more than 24 hours are cancelled.
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void expirePendingPayments() {
        log.info("Running scheduled task: Expire pending payments");
        try {
            int expiredCount = paymentService.expirePendingPayments(24);
            log.info("Expired {} pending payments", expiredCount);
        } catch (Exception e) {
            log.error("Error during payment expiration task", e);
        }
    }
}

