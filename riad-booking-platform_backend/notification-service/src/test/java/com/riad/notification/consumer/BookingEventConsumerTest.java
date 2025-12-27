package com.riad.notification.consumer;

import com.riad.notification.model.BookingEvent;
import com.riad.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BookingEventConsumerTest {

    @Test
    void shouldConsumeBookingConfirmedEvent() {
        NotificationService service = Mockito.mock(NotificationService.class);
        BookingEventConsumer consumer = new BookingEventConsumer(service);

        BookingEvent event = BookingEvent.builder()
                .eventType(BookingEvent.EventType.BOOKING_CONFIRMED)
                .build();

        consumer.consumeBookingEvent(event);

        Mockito.verify(service).sendBookingConfirmation(event);
    }
}

