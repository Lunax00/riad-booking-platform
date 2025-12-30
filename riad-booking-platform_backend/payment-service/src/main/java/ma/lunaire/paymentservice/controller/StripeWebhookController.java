package ma.lunaire.paymentservice.controller;

import ma.lunaire.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling Stripe webhooks.
 */
@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    private final PaymentService paymentService;

    /**
     * Handle Stripe webhook events.
     *
     * Configure this endpoint in your Stripe Dashboard:
     * https://dashboard.stripe.com/webhooks
     *
     * Events to subscribe to:
     * - payment_intent.succeeded
     * - payment_intent.payment_failed
     * - payment_intent.canceled
     * - charge.refunded
     */
    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        log.info("Received Stripe webhook");

        try {
            paymentService.handleStripeWebhook(payload, sigHeader);
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception e) {
            log.error("Webhook processing failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Webhook processing failed: " + e.getMessage());
        }
    }
}

