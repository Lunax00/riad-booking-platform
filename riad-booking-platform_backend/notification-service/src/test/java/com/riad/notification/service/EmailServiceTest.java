package com.riad.notification.service;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Test
    void shouldSendEmailWithoutException() {

        // Mock JavaMailSender
        JavaMailSender mailSender = mock(JavaMailSender.class);

        // REAL MimeMessage (important)
        MimeMessage mimeMessage =
                new MimeMessage(Session.getInstance(new Properties()));

        // Define behavior
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        EmailService emailService = new EmailService(mailSender);

        // Act + Assert (no exception = success)
        emailService.sendEmail("test@mail.com", "Subject", "<b>Hello</b>");

        // Verify mailSender.send was called
        verify(mailSender).send(mimeMessage);
    }
}
