package com.riad.notification.service;

import com.riad.notification.model.BookingEvent;
import com.riad.notification.model.Notification;
import com.riad.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;
    private final NotificationRepository notificationRepository;

    public void sendBookingConfirmation(BookingEvent event) {
        log.info("📧 Sending booking confirmation for booking: {}", event.getBookingId());

        String subject = "✅ Confirmation de réservation - " + event.getRiadName();
        String message = buildConfirmationMessage(event);

        sendNotification(event, subject, message, Notification.NotificationType.BOTH);
    }

    public void sendCancellationNotification(BookingEvent event) {
        log.info("📧 Sending cancellation notification for booking: {}", event.getBookingId());

        String subject = "❌ Annulation de réservation - " + event.getRiadName();
        String message = buildCancellationMessage(event);

        sendNotification(event, subject, message, Notification.NotificationType.BOTH);
    }

    public void sendPaymentConfirmation(BookingEvent event) {
        log.info("📧 Sending payment confirmation for booking: {}", event.getBookingId());

        String subject = "💳 Paiement reçu - " + event.getRiadName();
        String message = buildPaymentMessage(event);

        sendNotification(event, subject, message, Notification.NotificationType.EMAIL);
    }

    public void sendCheckInReminder(BookingEvent event) {
        log.info("📧 Sending check-in reminder for booking: {}", event.getBookingId());

        String subject = "🏨 Rappel Check-in - " + event.getRiadName();
        String message = buildCheckInReminderMessage(event);

        sendNotification(event, subject, message, Notification.NotificationType.BOTH);
    }

    private void sendNotification(BookingEvent event, String subject,
                                  String message, Notification.NotificationType type) {

        Notification notification = Notification.builder()
                .recipientEmail(event.getCustomerEmail())
                .recipientPhone(event.getCustomerPhone())
                .recipientName(event.getCustomerName())
                .subject(subject)
                .message(message)
                .type(type)
                .bookingId(event.getBookingId())
                .status(Notification.NotificationStatus.PENDING)
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        try {
            // Send Email
            if (type == Notification.NotificationType.EMAIL ||
                    type == Notification.NotificationType.BOTH) {
                emailService.sendEmail(event.getCustomerEmail(), subject, message);
            }


            notification.setStatus(Notification.NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());

        } catch (Exception e) {
            log.error("❌ Failed to send notification: {}", e.getMessage());
            notification.setStatus(Notification.NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
        }

        notification.setUpdatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    private String buildConfirmationMessage(BookingEvent event) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2>🎉 Réservation Confirmée !</h2>
                <p>Cher(e) <strong>%s</strong>,</p>
                <p>Nous avons le plaisir de confirmer votre réservation.</p>
                
                <div style="background-color: #f5f5f5; padding: 20px; border-radius: 10px;">
                    <h3>📋 Détails de la réservation:</h3>
                    <ul style="list-style: none;">
                        <li>🏨 <strong>Riad:</strong> %s</li>
                        <li>📅 <strong>Check-in:</strong> %s</li>
                        <li>📅 <strong>Check-out:</strong> %s</li>
                        <li>💰 <strong>Montant total:</strong> %.2f MAD</li>
                        <li>🔖 <strong>Référence:</strong> %s</li>
                    </ul>
                </div>
                
                <p>Nous avons hâte de vous accueillir à Marrakech ! ✨</p>
                
                <p>Cordialement,<br>
                <strong>L'équipe Riads Marrakech</strong></p>
            </body>
            </html>
            """,
                event.getCustomerName(),
                event.getRiadName(),
                event.getCheckInDate(),
                event.getCheckOutDate(),
                event.getTotalAmount(),
                event.getBookingId()
        );
    }

    private String buildCancellationMessage(BookingEvent event) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2>❌ Annulation de Réservation</h2>
                <p>Cher(e) <strong>%s</strong>,</p>
                <p>Votre réservation a été annulée avec succès.</p>
                
                <div style="background-color: #f5f5f5; padding: 20px; border-radius: 10px;">
                    <h3>📋 Détails:</h3>
                    <ul style="list-style: none;">
                        <li>🏨 <strong>Riad:</strong> %s</li>
                        <li>🔖 <strong>Référence:</strong> %s</li>
                    </ul>
                </div>
                
                <p>Si vous avez des questions, n'hésitez pas à nous contacter.</p>
                
                <p>Cordialement,<br>
                <strong>L'équipe Riads Marrakech</strong></p>
            </body>
            </html>
            """,
                event.getCustomerName(),
                event.getRiadName(),
                event.getBookingId()
        );
    }

    private String buildPaymentMessage(BookingEvent event) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2>💳 Paiement Reçu</h2>
                <p>Cher(e) <strong>%s</strong>,</p>
                <p>Nous avons bien reçu votre paiement de <strong>%.2f MAD</strong>.</p>
                
                <p>Cordialement,<br>
                <strong>L'équipe Riads Marrakech</strong></p>
            </body>
            </html>
            """,
                event.getCustomerName(),
                event.getTotalAmount()
        );
    }

    private String buildCheckInReminderMessage(BookingEvent event) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2>🏨 Rappel Check-in</h2>
                <p>Cher(e) <strong>%s</strong>,</p>
                <p>Votre check-in au <strong>%s</strong> est prévu pour le <strong>%s</strong>.</p>
                
                <p>Nous avons hâte de vous accueillir ! ✨</p>
                
                <p>Cordialement,<br>
                <strong>L'équipe Riads Marrakech</strong></p>
            </body>
            </html>
            """,
                event.getCustomerName(),
                event.getRiadName(),
                event.getCheckInDate()
        );
    }
}
