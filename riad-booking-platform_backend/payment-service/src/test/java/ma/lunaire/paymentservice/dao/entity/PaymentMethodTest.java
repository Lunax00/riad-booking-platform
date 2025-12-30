package ma.lunaire.paymentservice.dao.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for PaymentMethod enum.
 */
class PaymentMethodTest {

    @Test
    @DisplayName("Should have all expected payment methods")
    void shouldHaveAllExpectedMethods() {
        PaymentMethod[] methods = PaymentMethod.values();
        assertThat(methods).hasSize(6);
    }

    @Test
    @DisplayName("Should contain CREDIT_CARD method")
    void shouldContainCreditCardMethod() {
        assertThat(PaymentMethod.valueOf("CREDIT_CARD")).isEqualTo(PaymentMethod.CREDIT_CARD);
    }

    @Test
    @DisplayName("Should contain DEBIT_CARD method")
    void shouldContainDebitCardMethod() {
        assertThat(PaymentMethod.valueOf("DEBIT_CARD")).isEqualTo(PaymentMethod.DEBIT_CARD);
    }

    @Test
    @DisplayName("Should contain BANK_TRANSFER method")
    void shouldContainBankTransferMethod() {
        assertThat(PaymentMethod.valueOf("BANK_TRANSFER")).isEqualTo(PaymentMethod.BANK_TRANSFER);
    }

    @Test
    @DisplayName("Should contain PAYPAL method")
    void shouldContainPaypalMethod() {
        assertThat(PaymentMethod.valueOf("PAYPAL")).isEqualTo(PaymentMethod.PAYPAL);
    }

    @Test
    @DisplayName("Should contain CASH method")
    void shouldContainCashMethod() {
        assertThat(PaymentMethod.valueOf("CASH")).isEqualTo(PaymentMethod.CASH);
    }

    @Test
    @DisplayName("Should contain MOBILE_PAYMENT method")
    void shouldContainMobilePaymentMethod() {
        assertThat(PaymentMethod.valueOf("MOBILE_PAYMENT")).isEqualTo(PaymentMethod.MOBILE_PAYMENT);
    }
}

