package ma.lunaire.paymentservice.dto;

import ma.lunaire.paymentservice.dao.entity.PaymentMethod;
import ma.lunaire.paymentservice.dao.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for payment response with Stripe details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;
    private String paymentNumber;
    private Long bookingId;
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private String transactionId;
    private String description;
    private BigDecimal refundedAmount;
    private String failureReason;
    private String cardLastFour;
    private String cardBrand;
    private LocalDateTime paidAt;
    private LocalDateTime refundedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Stripe-specific fields
    private String stripePaymentIntentId;
    private String stripeCustomerId;
    private String stripePaymentMethodId;
    private String clientSecret;           // For frontend to complete payment
    private String receiptUrl;             // Stripe receipt URL
    private boolean requiresAction;        // If 3D Secure is needed
    private String nextActionUrl;          // URL for 3D Secure redirect
}
