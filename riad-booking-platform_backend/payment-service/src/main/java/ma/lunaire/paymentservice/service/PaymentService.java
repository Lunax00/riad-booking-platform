package ma.lunaire.paymentservice.service;

import ma.lunaire.paymentservice.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for payment operations with Stripe integration.
 */
public interface PaymentService {

    /**
     * Create a new payment intent with Stripe.
     * Returns a client secret for frontend to complete payment.
     */
    PaymentResponse createPayment(CreatePaymentRequest request);

    /**
     * Confirm a payment after frontend completion.
     */
    PaymentResponse confirmPayment(Long id);

    /**
     * Get payment by ID.
     */
    PaymentResponse getPaymentById(Long id);

    /**
     * Get payment by payment number.
     */
    PaymentResponse getPaymentByNumber(String paymentNumber);

    /**
     * Get payment by Stripe payment intent ID.
     */
    PaymentResponse getPaymentByStripeIntentId(String paymentIntentId);

    /**
     * Get payment by transaction ID.
     */
    PaymentResponse getPaymentByTransactionId(String transactionId);

    /**
     * Get all payments with pagination.
     */
    Page<PaymentResponse> getAllPayments(Pageable pageable);

    /**
     * Get payments by user ID.
     */
    Page<PaymentResponse> getPaymentsByUserId(Long userId, Pageable pageable);

    /**
     * Get payments by booking ID.
     */
    List<PaymentResponse> getPaymentsByBookingId(Long bookingId);

    /**
     * Search payments with criteria.
     */
    Page<PaymentResponse> searchPayments(PaymentSearchCriteria criteria, Pageable pageable);

    /**
     * Cancel a payment (cancels Stripe PaymentIntent).
     */
    PaymentResponse cancelPayment(Long id, String reason);

    /**
     * Refund a payment (full or partial) via Stripe.
     */
    PaymentResponse refundPayment(Long id, RefundRequest request);

    /**
     * Get total paid amount for a booking.
     */
    BigDecimal getTotalPaidAmountByBookingId(Long bookingId);

    /**
     * Check if booking has completed payment.
     */
    boolean hasCompletedPayment(Long bookingId);

    /**
     * Expire pending payments older than specified hours.
     */
    int expirePendingPayments(int hours);

    /**
     * Handle Stripe webhook event.
     */
    void handleStripeWebhook(String payload, String sigHeader);

    /**
     * Sync payment status with Stripe.
     */
    PaymentResponse syncPaymentStatus(Long id);
}
