package com.riad.notification.service;

import com.riad.notification.model.BookingEvent;
import com.riad.notification.model.Notification;
import com.riad.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Test
    void shouldSendBookingConfirmationAndSaveNotification() {

        // Mocks
        EmailService emailService = mock(EmailService.class);
        NotificationRepository repository = mock(NotificationRepository.class);

        NotificationService notificationService =
                new NotificationService(emailService, repository);

        BookingEvent event = BookingEvent.builder()
                .bookingId("B123")
                .customerEmail("test@mail.com")
                .customerName("John")
                .customerPhone("+212600000000")
                .riadName("Riad Atlas")
                .checkInDate("2025-01-01")
                .checkOutDate("2025-01-05")
                .totalAmount(500.0)
                .build();

        // Act
        notificationService.sendBookingConfirmation(event);

        // Capture saved notification
        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(repository).save(captor.capture());

        Notification saved = captor.getValue();

        // Assertions
        assertThat(saved.getStatus()).isEqualTo(Notification.NotificationStatus.SENT);
        assertThat(saved.getRecipientEmail()).isEqualTo("test@mail.com");

        // Verify email was sent
        verify(emailService).sendEmail(
                eq("test@mail.com"),
                contains("Confirmation"),
                anyString()
        );
    }
}
