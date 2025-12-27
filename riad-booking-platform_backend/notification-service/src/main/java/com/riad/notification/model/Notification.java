package com.riad.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    private String recipientEmail;
    private String recipientPhone;
    private String recipientName;
    private String subject;
    private String message;
    private NotificationType type;
    private NotificationStatus status;
    private String bookingId;
    private Integer retryCount;
    private String errorMessage;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum NotificationType {
        EMAIL,
        SMS,
        BOTH
    }

    public enum NotificationStatus {
        PENDING,
        SENT,
        FAILED,
        RETRY
    }
}

