package ma.lunaire.paymentservice.dao.entity;

/**
 * Enum representing the different statuses a payment can have.
 */
public enum PaymentStatus {
    PENDING,        // Payment initiated, awaiting processing
    PROCESSING,     // Payment is being processed
    COMPLETED,      // Payment completed successfully
    FAILED,         // Payment failed
    REFUNDED,       // Payment has been refunded
    PARTIALLY_REFUNDED, // Payment partially refunded
    CANCELLED       // Payment was cancelled
}
