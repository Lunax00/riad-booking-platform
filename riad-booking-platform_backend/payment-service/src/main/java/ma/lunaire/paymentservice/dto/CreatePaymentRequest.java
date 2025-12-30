package ma.lunaire.paymentservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.lunaire.paymentservice.dao.entity.PaymentMethod;

import java.math.BigDecimal;

/**
 * DTO for creating a new payment with Stripe.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentRequest {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Size(max = 3, message = "Currency code must be 3 characters")
    @Builder.Default
    private String currency = "MAD";

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    // Stripe-specific fields
    private String stripePaymentMethodId;  // For direct payment method
    private String stripeCustomerId;        // For saved customer

    // Customer details for Stripe (if no customer ID provided)
    @Email(message = "Invalid email format")
    private String customerEmail;
    private String customerName;

    // Return URL for 3D Secure / redirect-based payments
    private String returnUrl;
}
