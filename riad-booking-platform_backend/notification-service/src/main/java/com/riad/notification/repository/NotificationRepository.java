package com.riad.notification.repository;

import com.riad.notification.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByBookingId(String bookingId);

    List<Notification> findByStatus(Notification.NotificationStatus status);

    List<Notification> findByRecipientEmail(String email);

    List<Notification> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByStatusAndCreatedAtAfter(
            Notification.NotificationStatus status,
            LocalDateTime dateTime
    );
}
