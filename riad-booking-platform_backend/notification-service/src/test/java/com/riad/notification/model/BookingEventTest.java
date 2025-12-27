package com.riad.notification.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookingEventTest {

    @Test
    void shouldCreateBookingEvent() {
        BookingEvent event = BookingEvent.builder()
                .bookingId("B123")
                .customerEmail("test@mail.com")
                .eventType(BookingEvent.EventType.BOOKING_CONFIRMED)
                .build();

        assertThat(event.getBookingId()).isEqualTo("B123");
        assertThat(event.getEventType()).isEqualTo(BookingEvent.EventType.BOOKING_CONFIRMED);
    }
}

