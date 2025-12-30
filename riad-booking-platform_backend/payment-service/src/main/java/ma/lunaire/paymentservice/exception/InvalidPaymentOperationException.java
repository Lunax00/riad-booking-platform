package ma.lunaire.paymentservice.exception;

/**
 * Exception thrown when an invalid payment operation is attempted.
 */
public class InvalidPaymentOperationException extends RuntimeException {

    public InvalidPaymentOperationException(String message) {
        super(message);
    }
}

