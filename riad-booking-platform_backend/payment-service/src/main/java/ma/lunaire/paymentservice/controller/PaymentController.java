package ma.lunaire.paymentservice.controller;

import ma.lunaire.paymentservice.dto.*;
import ma.lunaire.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for payment operations with Stripe integration.
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Create a new payment with Stripe.
     * Returns a client secret for frontend to complete payment.
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Confirm a payment after frontend completion.
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(@PathVariable Long id) {
        PaymentResponse response = paymentService.confirmPayment(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment by payment number.
     */
    @GetMapping("/number/{paymentNumber}")
    public ResponseEntity<PaymentResponse> getPaymentByNumber(
            @PathVariable String paymentNumber) {
        PaymentResponse response = paymentService.getPaymentByNumber(paymentNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment by Stripe PaymentIntent ID.
     */
    @GetMapping("/stripe/{paymentIntentId}")
    public ResponseEntity<PaymentResponse> getPaymentByStripeIntentId(
            @PathVariable String paymentIntentId) {
        PaymentResponse response = paymentService.getPaymentByStripeIntentId(paymentIntentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment by transaction ID.
     */
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<PaymentResponse> getPaymentByTransactionId(
            @PathVariable String transactionId) {
        PaymentResponse response = paymentService.getPaymentByTransactionId(transactionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all payments with pagination.
     */
    @GetMapping
    public ResponseEntity<Page<PaymentResponse>> getAllPayments(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PaymentResponse> payments = paymentService.getAllPayments(pageable);
        return ResponseEntity.ok(payments);
    }

    /**
     * Get payments by user ID.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PaymentResponse>> getPaymentsByUserId(
            @PathVariable Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PaymentResponse> payments = paymentService.getPaymentsByUserId(userId, pageable);
        return ResponseEntity.ok(payments);
    }

    /**
     * Get payments by booking ID.
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByBookingId(
            @PathVariable Long bookingId) {
        List<PaymentResponse> payments = paymentService.getPaymentsByBookingId(bookingId);
        return ResponseEntity.ok(payments);
    }

    /**
     * Search payments with criteria.
     */
    @PostMapping("/search")
    public ResponseEntity<Page<PaymentResponse>> searchPayments(
            @RequestBody PaymentSearchCriteria criteria,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PaymentResponse> payments = paymentService.searchPayments(criteria, pageable);
        return ResponseEntity.ok(payments);
    }

    /**
     * Cancel a payment.
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        PaymentResponse response = paymentService.cancelPayment(id, reason);
        return ResponseEntity.ok(response);
    }

    /**
     * Refund a payment via Stripe.
     */
    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(
            @PathVariable Long id,
            @Valid @RequestBody RefundRequest request) {
        PaymentResponse response = paymentService.refundPayment(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Sync payment status with Stripe.
     */
    @PostMapping("/{id}/sync")
    public ResponseEntity<PaymentResponse> syncPaymentStatus(@PathVariable Long id) {
        PaymentResponse response = paymentService.syncPaymentStatus(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get total paid amount for a booking.
     */
    @GetMapping("/booking/{bookingId}/total")
    public ResponseEntity<BigDecimal> getTotalPaidAmount(@PathVariable Long bookingId) {
        BigDecimal total = paymentService.getTotalPaidAmountByBookingId(bookingId);
        return ResponseEntity.ok(total);
    }

    /**
     * Check if booking has completed payment.
     */
    @GetMapping("/booking/{bookingId}/completed")
    public ResponseEntity<Boolean> hasCompletedPayment(@PathVariable Long bookingId) {
        boolean hasPayment = paymentService.hasCompletedPayment(bookingId);
        return ResponseEntity.ok(hasPayment);
    }
}
