package com.code.reservationservice.dao.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a reservation for a riad in Marrakech.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reservations", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_riad_id", columnList = "riadId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_check_in_date", columnList = "checkInDate")
})
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reservationNumber;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long riadId;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Column(nullable = false)
    private Integer numberOfGuests;

    @Column(nullable = false)
    private Integer numberOfRooms;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.PENDING;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal depositAmount;

    @Column(length = 3)
    @Builder.Default
    private String currency = "MAD";

    @Column(length = 500)
    private String specialRequests;

    private String guestName;

    private String guestEmail;

    private String guestPhone;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    /**
     * Auto-generate reservation number before persisting.
     */
    @PrePersist
    public void generateReservationNumber() {
        if (this.reservationNumber == null) {
            this.reservationNumber = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
