package ma.lunaire.paymentservice.dao.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Payment entity.
 */
class PaymentTest {

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder()
                .id(1L)
                .paymentNumber("PAY-12345678")
                .bookingId(100L)
                .userId(1L)
                .amount(new BigDecimal("500.00"))
                .currency("MAD")
                .status(PaymentStatus.PENDING)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .description("Payment for booking")
                .build();
    }

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should create payment with builder")
        void shouldCreatePaymentWithBuilder() {
            assertThat(payment).isNotNull();
            assertThat(payment.getId()).isEqualTo(1L);
            assertThat(payment.getPaymentNumber()).isEqualTo("PAY-12345678");
            assertThat(payment.getBookingId()).isEqualTo(100L);
            assertThat(payment.getUserId()).isEqualTo(1L);
            assertThat(payment.getAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
            assertThat(payment.getCurrency()).isEqualTo("MAD");
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        }

        @Test
        @DisplayName("Should have default currency MAD")
        void shouldHaveDefaultCurrencyMAD() {
            Payment newPayment = Payment.builder()
                    .bookingId(100L)
                    .userId(1L)
                    .amount(new BigDecimal("100.00"))
                    .paymentMethod(PaymentMethod.CASH)
                    .build();

            assertThat(newPayment.getCurrency()).isEqualTo("MAD");
        }

        @Test
        @DisplayName("Should have default status PENDING")
        void shouldHaveDefaultStatusPending() {
            Payment newPayment = Payment.builder()
                    .bookingId(100L)
                    .userId(1L)
                    .amount(new BigDecimal("100.00"))
                    .paymentMethod(PaymentMethod.CASH)
                    .build();

            assertThat(newPayment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("Setter Tests")
    class SetterTests {

        @Test
        @DisplayName("Should set transaction ID")
        void shouldSetTransactionId() {
            payment.setTransactionId("TXN-ABCD1234");
            assertThat(payment.getTransactionId()).isEqualTo("TXN-ABCD1234");
        }

        @Test
        @DisplayName("Should set paid at timestamp")
        void shouldSetPaidAtTimestamp() {
            LocalDateTime paidAt = LocalDateTime.now();
            payment.setPaidAt(paidAt);
            assertThat(payment.getPaidAt()).isEqualTo(paidAt);
        }

        @Test
        @DisplayName("Should set refund details")
        void shouldSetRefundDetails() {
            LocalDateTime refundedAt = LocalDateTime.now();
            payment.setRefundedAmount(new BigDecimal("100.00"));
            payment.setRefundedAt(refundedAt);

            assertThat(payment.getRefundedAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
            assertThat(payment.getRefundedAt()).isEqualTo(refundedAt);
        }

        @Test
        @DisplayName("Should set card details")
        void shouldSetCardDetails() {
            payment.setCardLastFour("4242");
            payment.setCardBrand("Visa");

            assertThat(payment.getCardLastFour()).isEqualTo("4242");
            assertThat(payment.getCardBrand()).isEqualTo("Visa");
        }

        @Test
        @DisplayName("Should set failure reason")
        void shouldSetFailureReason() {
            payment.setFailureReason("Insufficient funds");
            assertThat(payment.getFailureReason()).isEqualTo("Insufficient funds");
        }
    }

    @Nested
    @DisplayName("Pre-Persist Tests")
    class PrePersistTests {

        @Test
        @DisplayName("Should generate payment number if null")
        void shouldGeneratePaymentNumberIfNull() {
            Payment newPayment = Payment.builder()
                    .bookingId(100L)
                    .userId(1L)
                    .amount(new BigDecimal("100.00"))
                    .paymentMethod(PaymentMethod.CASH)
                    .build();

            assertThat(newPayment.getPaymentNumber()).isNull();

            newPayment.generatePaymentNumber();

            assertThat(newPayment.getPaymentNumber()).isNotNull();
            assertThat(newPayment.getPaymentNumber()).startsWith("PAY-");
            assertThat(newPayment.getPaymentNumber()).hasSize(12);
        }

        @Test
        @DisplayName("Should not overwrite existing payment number")
        void shouldNotOverwriteExistingPaymentNumber() {
            payment.generatePaymentNumber();
            assertThat(payment.getPaymentNumber()).isEqualTo("PAY-12345678");
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should have same field values when built identically")
        void shouldHaveSameFieldValuesWhenBuiltIdentically() {
            Payment payment1 = Payment.builder()
                    .id(1L)
                    .paymentNumber("PAY-12345678")
                    .bookingId(100L)
                    .build();

            Payment payment2 = Payment.builder()
                    .id(1L)
                    .paymentNumber("PAY-12345678")
                    .bookingId(100L)
                    .build();

            assertThat(payment1.getId()).isEqualTo(payment2.getId());
            assertThat(payment1.getPaymentNumber()).isEqualTo(payment2.getPaymentNumber());
            assertThat(payment1.getBookingId()).isEqualTo(payment2.getBookingId());
        }
    }
}

