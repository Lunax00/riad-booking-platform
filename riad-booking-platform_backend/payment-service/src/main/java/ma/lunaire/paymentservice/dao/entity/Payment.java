package ma.lunaire.paymentservice.dao.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a payment for a riad booking with Stripe integration.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_booking_id", columnList = "bookingId"),
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_transaction_id", columnList = "transactionId"),
    @Index(name = "idx_stripe_payment_intent_id", columnList = "stripePaymentIntentId")
})
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String paymentNumber;

    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(length = 3)
    @Builder.Default
    private String currency = "MAD";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(unique = true)
    private String transactionId;

    @Column(length = 500)
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal refundedAmount;

    private String failureReason;

    private String gatewayResponse;

    @Column(length = 4)
    private String cardLastFour;

    private String cardBrand;

    private LocalDateTime paidAt;

    private LocalDateTime refundedAt;

    // Stripe-specific fields
    @Column(unique = true)
    private String stripePaymentIntentId;

    private String stripeCustomerId;

    private String stripePaymentMethodId;

    @Column(length = 500)
    private String stripeClientSecret;

    private String stripeReceiptUrl;

    private String stripeRefundId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    /**
     * Auto-generate payment number before persisting.
     */
    @PrePersist
    public void generatePaymentNumber() {
        if (this.paymentNumber == null) {
            this.paymentNumber = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
