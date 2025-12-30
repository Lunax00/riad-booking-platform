package ma.lunaire.paymentservice.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for custom exceptions.
 */
class ExceptionTest {

    @Test
    @DisplayName("PaymentNotFoundException with message")
    void paymentNotFoundExceptionWithMessage() {
        PaymentNotFoundException exception = new PaymentNotFoundException("Payment not found");
        assertThat(exception.getMessage()).isEqualTo("Payment not found");
    }

    @Test
    @DisplayName("PaymentNotFoundException with ID")
    void paymentNotFoundExceptionWithId() {
        PaymentNotFoundException exception = new PaymentNotFoundException(123L);
        assertThat(exception.getMessage()).isEqualTo("Payment not found with id: 123");
    }

    @Test
    @DisplayName("PaymentNotFoundException with field and value")
    void paymentNotFoundExceptionWithFieldAndValue() {
        PaymentNotFoundException exception = new PaymentNotFoundException("paymentNumber", "PAY-123");
        assertThat(exception.getMessage()).isEqualTo("Payment not found with paymentNumber: PAY-123");
    }

    @Test
    @DisplayName("PaymentProcessingException with message")
    void paymentProcessingExceptionWithMessage() {
        PaymentProcessingException exception = new PaymentProcessingException("Processing failed");
        assertThat(exception.getMessage()).isEqualTo("Processing failed");
    }

    @Test
    @DisplayName("PaymentProcessingException with cause")
    void paymentProcessingExceptionWithCause() {
        RuntimeException cause = new RuntimeException("Root cause");
        PaymentProcessingException exception = new PaymentProcessingException("Processing failed", cause);
        assertThat(exception.getMessage()).isEqualTo("Processing failed");
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("InvalidPaymentOperationException with message")
    void invalidPaymentOperationExceptionWithMessage() {
        InvalidPaymentOperationException exception = new InvalidPaymentOperationException("Invalid operation");
        assertThat(exception.getMessage()).isEqualTo("Invalid operation");
    }
}

