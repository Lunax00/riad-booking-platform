package com.riad.notification.consumer;

import com.riad.notification.model.BookingEvent;
import com.riad.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventConsumer {

    private final NotificationService notificationService;

    /**
     * Kafka listener that consumes booking events
     */
    @KafkaListener(
            topics = "booking-events",
            groupId = "notification-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeBookingEvent(BookingEvent event) {

        log.info("📩 Received booking event: {}", event);

        if (event == null || event.getEventType() == null) {
            log.warn("⚠️ Received null or invalid booking event");
            return;
        }

        try {
            switch (event.getEventType()) {

                case BOOKING_CONFIRMED -> {
                    log.info("➡️ Processing BOOKING_CONFIRMED event");
                    notificationService.sendBookingConfirmation(event);
                }

                case BOOKING_CANCELLED -> {
                    log.info("➡️ Processing BOOKING_CANCELLED event");
                    notificationService.sendCancellationNotification(event);
                }

                case PAYMENT_RECEIVED -> {
                    log.info("➡️ Processing PAYMENT_RECEIVED event");
                    notificationService.sendPaymentConfirmation(event);
                }

                case REMINDER_CHECKIN -> {
                    log.info("➡️ Processing REMINDER_CHECKIN event");
                    notificationService.sendCheckInReminder(event);
                }

                case REMINDER_CHECKOUT -> {
                    log.info("➡️ Processing REMINDER_CHECKOUT event");
                    // You can implement this later if needed
                    log.info("ℹ️ Check-out reminder not implemented yet");
                }

                default -> log.warn("⚠️ Unknown event type: {}", event.getEventType());
            }

        } catch (Exception e) {
            log.error("❌ Error while processing booking event: {}", event, e);
        }
    }
}


