package ma.lunaire.paymentservice.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerSearchResult;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.*;
import ma.lunaire.paymentservice.exception.PaymentProcessingException;
import ma.lunaire.paymentservice.service.StripeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of StripeService for real Stripe API operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StripeServiceImpl implements StripeService {

    @Override
    public PaymentIntent createPaymentIntent(
            Long amount,
            String currency,
            String customerId,
            String paymentMethodId,
            String description,
            Map<String, String> metadata
    ) {
        try {
            log.info("Creating PaymentIntent for amount: {} {}", amount, currency);

            PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                    .setAmount(amount)
                    .setCurrency(currency.toLowerCase())
                    .setDescription(description)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    );

            if (customerId != null && !customerId.isEmpty()) {
                paramsBuilder.setCustomer(customerId);
            }

            if (paymentMethodId != null && !paymentMethodId.isEmpty()) {
                paramsBuilder.setPaymentMethod(paymentMethodId);
            }

            if (metadata != null && !metadata.isEmpty()) {
                paramsBuilder.putAllMetadata(metadata);
            }

            PaymentIntent paymentIntent = PaymentIntent.create(paramsBuilder.build());

            log.info("Created PaymentIntent: {}", paymentIntent.getId());
            return paymentIntent;

        } catch (StripeException e) {
            log.error("Failed to create PaymentIntent: {}", e.getMessage());
            throw new PaymentProcessingException("Failed to create payment: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentIntent confirmPaymentIntent(String paymentIntentId, String paymentMethodId) {
        try {
            log.info("Confirming PaymentIntent: {}", paymentIntentId);

            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            PaymentIntentConfirmParams.Builder paramsBuilder = PaymentIntentConfirmParams.builder();

            if (paymentMethodId != null && !paymentMethodId.isEmpty()) {
                paramsBuilder.setPaymentMethod(paymentMethodId);
            }

            PaymentIntent confirmedIntent = paymentIntent.confirm(paramsBuilder.build());

            log.info("Confirmed PaymentIntent: {}, Status: {}", confirmedIntent.getId(), confirmedIntent.getStatus());
            return confirmedIntent;

        } catch (StripeException e) {
            log.error("Failed to confirm PaymentIntent: {}", e.getMessage());
            throw new PaymentProcessingException("Failed to confirm payment: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentIntent cancelPaymentIntent(String paymentIntentId) {
        try {
            log.info("Cancelling PaymentIntent: {}", paymentIntentId);

            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            PaymentIntent cancelledIntent = paymentIntent.cancel();

            log.info("Cancelled PaymentIntent: {}", cancelledIntent.getId());
            return cancelledIntent;

        } catch (StripeException e) {
            log.error("Failed to cancel PaymentIntent: {}", e.getMessage());
            throw new PaymentProcessingException("Failed to cancel payment: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) {
        try {
            return PaymentIntent.retrieve(paymentIntentId);
        } catch (StripeException e) {
            log.error("Failed to retrieve PaymentIntent: {}", e.getMessage());
            throw new PaymentProcessingException("Failed to retrieve payment: " + e.getMessage(), e);
        }
    }

    @Override
    public Refund createRefund(String paymentIntentId, Long amount, String reason) {
        try {
            log.info("Creating refund for PaymentIntent: {}, amount: {}", paymentIntentId, amount);

            RefundCreateParams.Builder paramsBuilder = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId);

            if (amount != null) {
                paramsBuilder.setAmount(amount);
            }

            if (reason != null && !reason.isEmpty()) {
                // Map reason to Stripe's predefined reasons
                if (reason.toLowerCase().contains("duplicate")) {
                    paramsBuilder.setReason(RefundCreateParams.Reason.DUPLICATE);
                } else if (reason.toLowerCase().contains("fraud")) {
                    paramsBuilder.setReason(RefundCreateParams.Reason.FRAUDULENT);
                } else {
                    paramsBuilder.setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER);
                }
            }

            Refund refund = Refund.create(paramsBuilder.build());

            log.info("Created Refund: {}", refund.getId());
            return refund;

        } catch (StripeException e) {
            log.error("Failed to create refund: {}", e.getMessage());
            throw new PaymentProcessingException("Failed to refund payment: " + e.getMessage(), e);
        }
    }

    @Override
    public Customer createOrRetrieveCustomer(String email, String name, Map<String, String> metadata) {
        try {
            // First, try to find existing customer by email
            CustomerSearchParams searchParams = CustomerSearchParams.builder()
                    .setQuery("email:'" + email + "'")
                    .build();

            CustomerSearchResult searchResult = Customer.search(searchParams);

            if (!searchResult.getData().isEmpty()) {
                log.info("Found existing customer for email: {}", email);
                return searchResult.getData().get(0);
            }

            // Create new customer
            log.info("Creating new customer for email: {}", email);

            CustomerCreateParams.Builder paramsBuilder = CustomerCreateParams.builder()
                    .setEmail(email);

            if (name != null && !name.isEmpty()) {
                paramsBuilder.setName(name);
            }

            if (metadata != null && !metadata.isEmpty()) {
                paramsBuilder.putAllMetadata(metadata);
            }

            Customer customer = Customer.create(paramsBuilder.build());

            log.info("Created customer: {}", customer.getId());
            return customer;

        } catch (StripeException e) {
            log.error("Failed to create/retrieve customer: {}", e.getMessage());
            throw new PaymentProcessingException("Failed to process customer: " + e.getMessage(), e);
        }
    }

    @Override
    public Long convertToStripeAmount(BigDecimal amount) {
        // Stripe uses smallest currency unit (centimes for MAD)
        // 1 MAD = 100 centimes
        return amount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
    }

    @Override
    public BigDecimal convertFromStripeAmount(Long stripeAmount) {
        return BigDecimal.valueOf(stripeAmount)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}

