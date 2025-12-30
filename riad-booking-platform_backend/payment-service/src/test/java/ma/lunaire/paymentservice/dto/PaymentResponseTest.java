package ma.lunaire.paymentservice.dto;

import ma.lunaire.paymentservice.dao.entity.PaymentMethod;
import ma.lunaire.paymentservice.dao.entity.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for PaymentResponse DTO.
 */
class PaymentResponseTest {

    @Test
    @DisplayName("Should create response with builder")
    void shouldCreateResponseWithBuilder() {
        LocalDateTime now = LocalDateTime.now();

        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .paymentNumber("PAY-12345678")
                .bookingId(100L)
                .userId(1L)
                .amount(new BigDecimal("500.00"))
                .currency("MAD")
                .status(PaymentStatus.COMPLETED)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .transactionId("TXN-ABCD1234")
                .description("Payment for booking")
                .paidAt(now)
                .createdAt(now)
                .build();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getPaymentNumber()).isEqualTo("PAY-12345678");
        assertThat(response.getBookingId()).isEqualTo(100L);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(response.getCurrency()).isEqualTo("MAD");
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(response.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(response.getTransactionId()).isEqualTo("TXN-ABCD1234");
    }

    @Test
    @DisplayName("Should set refund details")
    void shouldSetRefundDetails() {
        LocalDateTime now = LocalDateTime.now();

        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .status(PaymentStatus.PARTIALLY_REFUNDED)
                .refundedAmount(new BigDecimal("100.00"))
                .refundedAt(now)
                .build();

        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PARTIALLY_REFUNDED);
        assertThat(response.getRefundedAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(response.getRefundedAt()).isEqualTo(now);
    }
}

