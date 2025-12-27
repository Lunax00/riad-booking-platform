package com.riad.notification.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest {

    @Test
    void shouldBuildNotificationCorrectly() {
        Notification notification = Notification.builder()
                .recipientEmail("test@mail.com")
                .subject("Test")
                .status(Notification.NotificationStatus.PENDING)
                .build();

        assertThat(notification.getRecipientEmail()).isEqualTo("test@mail.com");
        assertThat(notification.getStatus()).isEqualTo(Notification.NotificationStatus.PENDING);
    }
}

