package ma.lunaire.paymentservice.service;

import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Service interface for Stripe API operations.
 */
public interface StripeService {

    /**
     * Create a PaymentIntent for a payment.
     *
     * @param amount      Amount in the smallest currency unit (e.g., centimes for MAD)
     * @param currency    Currency code (e.g., "mad")
     * @param customerId  Optional Stripe customer ID
     * @param paymentMethodId Optional payment method ID
     * @param description Payment description
     * @param metadata    Additional metadata
     * @return Created PaymentIntent
     */
    PaymentIntent createPaymentIntent(
            Long amount,
            String currency,
            String customerId,
            String paymentMethodId,
            String description,
            Map<String, String> metadata
    );

    /**
     * Confirm a PaymentIntent.
     *
     * @param paymentIntentId The ID of the PaymentIntent to confirm
     * @param paymentMethodId Optional payment method ID
     * @return Updated PaymentIntent
     */
    PaymentIntent confirmPaymentIntent(String paymentIntentId, String paymentMethodId);

    /**
     * Cancel a PaymentIntent.
     *
     * @param paymentIntentId The ID of the PaymentIntent to cancel
     * @return Cancelled PaymentIntent
     */
    PaymentIntent cancelPaymentIntent(String paymentIntentId);

    /**
     * Retrieve a PaymentIntent.
     *
     * @param paymentIntentId The ID of the PaymentIntent
     * @return PaymentIntent
     */
    PaymentIntent retrievePaymentIntent(String paymentIntentId);

    /**
     * Create a refund for a PaymentIntent.
     *
     * @param paymentIntentId The ID of the PaymentIntent to refund
     * @param amount          Amount to refund in smallest currency unit (null for full refund)
     * @param reason          Reason for refund
     * @return Created Refund
     */
    Refund createRefund(String paymentIntentId, Long amount, String reason);

    /**
     * Create or retrieve a Stripe customer.
     *
     * @param email Customer email
     * @param name  Customer name
     * @param metadata Additional metadata
     * @return Customer
     */
    Customer createOrRetrieveCustomer(String email, String name, Map<String, String> metadata);

    /**
     * Convert amount from BigDecimal to Stripe's smallest unit (centimes).
     *
     * @param amount BigDecimal amount
     * @return Amount in smallest currency unit
     */
    Long convertToStripeAmount(BigDecimal amount);

    /**
     * Convert Stripe amount back to BigDecimal.
     *
     * @param stripeAmount Amount in smallest currency unit
     * @return BigDecimal amount
     */
    BigDecimal convertFromStripeAmount(Long stripeAmount);
}

