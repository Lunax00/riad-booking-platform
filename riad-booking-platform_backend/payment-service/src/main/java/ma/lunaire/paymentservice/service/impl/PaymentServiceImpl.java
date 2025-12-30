package ma.lunaire.paymentservice.service.impl;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import ma.lunaire.paymentservice.config.StripeConfig;
import ma.lunaire.paymentservice.dao.entity.Payment;
import ma.lunaire.paymentservice.dao.entity.PaymentStatus;
import ma.lunaire.paymentservice.dao.repository.PaymentRepository;
import ma.lunaire.paymentservice.dto.*;
import ma.lunaire.paymentservice.exception.InvalidPaymentOperationException;
import ma.lunaire.paymentservice.exception.PaymentNotFoundException;
import ma.lunaire.paymentservice.exception.PaymentProcessingException;
import ma.lunaire.paymentservice.mapper.PaymentMapper;
import ma.lunaire.paymentservice.service.PaymentService;
import ma.lunaire.paymentservice.service.StripeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of PaymentService with real Stripe integration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;
    private final StripeConfig stripeConfig;

    @Override
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        log.info("Creating Stripe payment for booking {} by user {}", request.getBookingId(), request.getUserId());

        // Create or retrieve Stripe customer if email provided
        String stripeCustomerId = request.getStripeCustomerId();
        if (stripeCustomerId == null && request.getCustomerEmail() != null) {
            Map<String, String> customerMetadata = new HashMap<>();
            customerMetadata.put("userId", request.getUserId().toString());
            customerMetadata.put("bookingId", request.getBookingId().toString());

            Customer customer = stripeService.createOrRetrieveCustomer(
                    request.getCustomerEmail(),
                    request.getCustomerName(),
                    customerMetadata
            );
            stripeCustomerId = customer.getId();
        }

        // Prepare metadata for Stripe
        Map<String, String> metadata = new HashMap<>();
        metadata.put("bookingId", request.getBookingId().toString());
        metadata.put("userId", request.getUserId().toString());

        // Convert amount to Stripe format (centimes)
        Long stripeAmount = stripeService.convertToStripeAmount(request.getAmount());

        // Create PaymentIntent with Stripe
        PaymentIntent paymentIntent = stripeService.createPaymentIntent(
                stripeAmount,
                request.getCurrency(),
                stripeCustomerId,
                request.getStripePaymentMethodId(),
                request.getDescription() != null ? request.getDescription() :
                        "Payment for booking #" + request.getBookingId(),
                metadata
        );

        // Create local payment record
        Payment payment = paymentMapper.toEntity(request);
        payment.setStripePaymentIntentId(paymentIntent.getId());
        payment.setStripeClientSecret(paymentIntent.getClientSecret());
        payment.setStripeCustomerId(stripeCustomerId);
        payment.setStripePaymentMethodId(request.getStripePaymentMethodId());
        payment.setTransactionId(paymentIntent.getId());

        // Set status based on PaymentIntent status
        payment.setStatus(mapStripeStatusToPaymentStatus(paymentIntent.getStatus()));

        Payment savedPayment = paymentRepository.save(payment);

        log.info("Created payment with number: {}, Stripe PI: {}",
                savedPayment.getPaymentNumber(), paymentIntent.getId());

        return buildPaymentResponse(savedPayment, paymentIntent);
    }

    @Override
    public PaymentResponse confirmPayment(Long id) {
        log.info("Confirming payment with id: {}", id);

        Payment payment = findPaymentById(id);

        if (payment.getStripePaymentIntentId() == null) {
            throw new InvalidPaymentOperationException("Payment has no associated Stripe PaymentIntent");
        }

        // Confirm with Stripe
        PaymentIntent paymentIntent = stripeService.confirmPaymentIntent(
                payment.getStripePaymentIntentId(),
                payment.getStripePaymentMethodId()
        );

        // Update local payment record
        updatePaymentFromStripe(payment, paymentIntent);
        Payment updatedPayment = paymentRepository.save(payment);

        log.info("Confirmed payment: {}, Status: {}", id, updatedPayment.getStatus());
        return buildPaymentResponse(updatedPayment, paymentIntent);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = findPaymentById(id);
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByNumber(String paymentNumber) {
        Payment payment = paymentRepository.findByPaymentNumber(paymentNumber)
                .orElseThrow(() -> new PaymentNotFoundException("paymentNumber", paymentNumber));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByStripeIntentId(String paymentIntentId) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new PaymentNotFoundException("stripePaymentIntentId", paymentIntentId));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new PaymentNotFoundException("transactionId", transactionId));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .map(paymentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPaymentsByUserId(Long userId, Pageable pageable) {
        return paymentRepository.findByUserId(userId, pageable)
                .map(paymentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> searchPayments(PaymentSearchCriteria criteria, Pageable pageable) {
        if (criteria.getUserId() != null && criteria.getStatus() != null) {
            return paymentRepository.findByUserIdAndStatus(criteria.getUserId(), criteria.getStatus(), pageable)
                    .map(paymentMapper::toResponse);
        } else if (criteria.getUserId() != null) {
            return paymentRepository.findByUserId(criteria.getUserId(), pageable)
                    .map(paymentMapper::toResponse);
        }
        return paymentRepository.findAll(pageable).map(paymentMapper::toResponse);
    }

    @Override
    public PaymentResponse cancelPayment(Long id, String reason) {
        log.info("Cancelling payment with id: {}", id);

        Payment payment = findPaymentById(id);

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidPaymentOperationException(
                    "Can only cancel PENDING payments. Current status: " + payment.getStatus());
        }

        // Cancel with Stripe if PaymentIntent exists
        if (payment.getStripePaymentIntentId() != null) {
            stripeService.cancelPaymentIntent(payment.getStripePaymentIntentId());
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setFailureReason(reason);
        Payment cancelledPayment = paymentRepository.save(payment);

        log.info("Cancelled payment with id: {}", id);
        return paymentMapper.toResponse(cancelledPayment);
    }

    @Override
    public PaymentResponse refundPayment(Long id, RefundRequest request) {
        log.info("Refunding payment with id: {}, amount: {}", id, request.getAmount());

        Payment payment = findPaymentById(id);

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new InvalidPaymentOperationException(
                    "Can only refund COMPLETED payments. Current status: " + payment.getStatus());
        }

        if (payment.getStripePaymentIntentId() == null) {
            throw new InvalidPaymentOperationException("Payment has no associated Stripe PaymentIntent");
        }

        BigDecimal currentRefunded = payment.getRefundedAmount() != null ?
                payment.getRefundedAmount() : BigDecimal.ZERO;
        BigDecimal totalRefund = currentRefunded.add(request.getAmount());

        if (totalRefund.compareTo(payment.getAmount()) > 0) {
            throw new InvalidPaymentOperationException(
                    "Total refund amount cannot exceed payment amount. Requested: " + totalRefund +
                    ", Max: " + payment.getAmount());
        }

        // Create refund with Stripe
        Long stripeRefundAmount = stripeService.convertToStripeAmount(request.getAmount());
        Refund refund = stripeService.createRefund(
                payment.getStripePaymentIntentId(),
                stripeRefundAmount,
                request.getReason()
        );

        payment.setRefundedAmount(totalRefund);
        payment.setRefundedAt(LocalDateTime.now());
        payment.setStripeRefundId(refund.getId());

        if (totalRefund.compareTo(payment.getAmount()) == 0) {
            payment.setStatus(PaymentStatus.REFUNDED);
        } else {
            payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        }

        Payment refundedPayment = paymentRepository.save(payment);

        log.info("Refunded payment with id: {}, Stripe Refund: {}, total refunded: {}",
                id, refund.getId(), totalRefund);
        return paymentMapper.toResponse(refundedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPaidAmountByBookingId(Long bookingId) {
        return paymentRepository.getTotalPaidAmountByBookingId(bookingId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasCompletedPayment(Long bookingId) {
        return paymentRepository.existsByBookingIdAndStatus(bookingId, PaymentStatus.COMPLETED);
    }

    @Override
    public int expirePendingPayments(int hours) {
        log.info("Expiring pending payments older than {} hours", hours);

        LocalDateTime expirationDate = LocalDateTime.now().minusHours(hours);
        List<Payment> pendingPayments = paymentRepository.findPendingPaymentsOlderThan(expirationDate);

        int expiredCount = 0;
        for (Payment payment : pendingPayments) {
            try {
                // Cancel with Stripe
                if (payment.getStripePaymentIntentId() != null) {
                    stripeService.cancelPaymentIntent(payment.getStripePaymentIntentId());
                }
                payment.setStatus(PaymentStatus.CANCELLED);
                payment.setFailureReason("Payment expired - not completed within " + hours + " hours");
                paymentRepository.save(payment);
                expiredCount++;
            } catch (Exception e) {
                log.error("Failed to expire payment {}: {}", payment.getId(), e.getMessage());
            }
        }

        log.info("Expired {} pending payments", expiredCount);
        return expiredCount;
    }

    @Override
    public void handleStripeWebhook(String payload, String sigHeader) {
        log.info("Processing Stripe webhook");

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeConfig.getWebhookSecret());
        } catch (SignatureVerificationException e) {
            log.error("Webhook signature verification failed: {}", e.getMessage());
            throw new PaymentProcessingException("Invalid webhook signature", e);
        }

        log.info("Received Stripe event: {}", event.getType());

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);

        if (stripeObject == null) {
            log.warn("Unable to deserialize Stripe object from webhook");
            return;
        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
                handlePaymentIntentSucceeded((PaymentIntent) stripeObject);
                break;
            case "payment_intent.payment_failed":
                handlePaymentIntentFailed((PaymentIntent) stripeObject);
                break;
            case "payment_intent.canceled":
                handlePaymentIntentCanceled((PaymentIntent) stripeObject);
                break;
            case "charge.refunded":
                handleChargeRefunded((Charge) stripeObject);
                break;
            default:
                log.info("Unhandled event type: {}", event.getType());
        }
    }

    @Override
    public PaymentResponse syncPaymentStatus(Long id) {
        log.info("Syncing payment status with Stripe for id: {}", id);

        Payment payment = findPaymentById(id);

        if (payment.getStripePaymentIntentId() == null) {
            throw new InvalidPaymentOperationException("Payment has no associated Stripe PaymentIntent");
        }

        PaymentIntent paymentIntent = stripeService.retrievePaymentIntent(payment.getStripePaymentIntentId());
        updatePaymentFromStripe(payment, paymentIntent);
        Payment updatedPayment = paymentRepository.save(payment);

        log.info("Synced payment {}, Status: {}", id, updatedPayment.getStatus());
        return paymentMapper.toResponse(updatedPayment);
    }

    // Private helper methods

    private Payment findPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    private PaymentStatus mapStripeStatusToPaymentStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "requires_payment_method", "requires_confirmation", "requires_action" -> PaymentStatus.PENDING;
            case "processing" -> PaymentStatus.PROCESSING;
            case "succeeded" -> PaymentStatus.COMPLETED;
            case "canceled" -> PaymentStatus.CANCELLED;
            default -> PaymentStatus.PENDING;
        };
    }

    private void updatePaymentFromStripe(Payment payment, PaymentIntent paymentIntent) {
        payment.setStatus(mapStripeStatusToPaymentStatus(paymentIntent.getStatus()));

        if ("succeeded".equals(paymentIntent.getStatus())) {
            payment.setPaidAt(LocalDateTime.now());

            // Get charge details if available
            if (paymentIntent.getLatestCharge() != null) {
                try {
                    Charge charge = Charge.retrieve(paymentIntent.getLatestCharge());
                    payment.setStripeReceiptUrl(charge.getReceiptUrl());

                    // Get card details
                    if (charge.getPaymentMethodDetails() != null &&
                        charge.getPaymentMethodDetails().getCard() != null) {
                        payment.setCardLastFour(charge.getPaymentMethodDetails().getCard().getLast4());
                        payment.setCardBrand(charge.getPaymentMethodDetails().getCard().getBrand());
                    }
                } catch (Exception e) {
                    log.warn("Failed to retrieve charge details: {}", e.getMessage());
                }
            }
        } else if ("canceled".equals(paymentIntent.getStatus())) {
            payment.setFailureReason(paymentIntent.getCancellationReason());
        }
    }

    private PaymentResponse buildPaymentResponse(Payment payment, PaymentIntent paymentIntent) {
        PaymentResponse response = paymentMapper.toResponse(payment);
        response.setStripePaymentIntentId(paymentIntent.getId());
        response.setClientSecret(paymentIntent.getClientSecret());
        response.setRequiresAction("requires_action".equals(paymentIntent.getStatus()));

        if (paymentIntent.getNextAction() != null &&
            paymentIntent.getNextAction().getRedirectToUrl() != null) {
            response.setNextActionUrl(paymentIntent.getNextAction().getRedirectToUrl().getUrl());
        }

        return response;
    }

    private void handlePaymentIntentSucceeded(PaymentIntent paymentIntent) {
        log.info("Handling payment_intent.succeeded for: {}", paymentIntent.getId());

        paymentRepository.findByStripePaymentIntentId(paymentIntent.getId())
                .ifPresent(payment -> {
                    updatePaymentFromStripe(payment, paymentIntent);
                    paymentRepository.save(payment);
                    log.info("Updated payment {} to COMPLETED", payment.getPaymentNumber());
                });
    }

    private void handlePaymentIntentFailed(PaymentIntent paymentIntent) {
        log.info("Handling payment_intent.payment_failed for: {}", paymentIntent.getId());

        paymentRepository.findByStripePaymentIntentId(paymentIntent.getId())
                .ifPresent(payment -> {
                    payment.setStatus(PaymentStatus.FAILED);
                    if (paymentIntent.getLastPaymentError() != null) {
                        payment.setFailureReason(paymentIntent.getLastPaymentError().getMessage());
                    }
                    paymentRepository.save(payment);
                    log.info("Updated payment {} to FAILED", payment.getPaymentNumber());
                });
    }

    private void handlePaymentIntentCanceled(PaymentIntent paymentIntent) {
        log.info("Handling payment_intent.canceled for: {}", paymentIntent.getId());

        paymentRepository.findByStripePaymentIntentId(paymentIntent.getId())
                .ifPresent(payment -> {
                    payment.setStatus(PaymentStatus.CANCELLED);
                    payment.setFailureReason(paymentIntent.getCancellationReason());
                    paymentRepository.save(payment);
                    log.info("Updated payment {} to CANCELLED", payment.getPaymentNumber());
                });
    }

    private void handleChargeRefunded(Charge charge) {
        log.info("Handling charge.refunded for: {}", charge.getId());

        if (charge.getPaymentIntent() != null) {
            paymentRepository.findByStripePaymentIntentId(charge.getPaymentIntent())
                    .ifPresent(payment -> {
                        BigDecimal refundedAmount = stripeService.convertFromStripeAmount(charge.getAmountRefunded());
                        payment.setRefundedAmount(refundedAmount);
                        payment.setRefundedAt(LocalDateTime.now());

                        if (charge.getRefunded()) {
                            payment.setStatus(PaymentStatus.REFUNDED);
                        } else {
                            payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
                        }

                        paymentRepository.save(payment);
                        log.info("Updated payment {} refund status", payment.getPaymentNumber());
                    });
        }
    }
}
