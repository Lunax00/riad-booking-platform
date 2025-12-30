package ma.lunaire.paymentservice.dto;

import ma.lunaire.paymentservice.dao.entity.PaymentMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for CreatePaymentRequest DTO.
 */
class CreatePaymentRequestTest {

    @Test
    @DisplayName("Should create request with builder")
    void shouldCreateRequestWithBuilder() {
        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .bookingId(100L)
                .userId(1L)
                .amount(new BigDecimal("500.00"))
                .currency("MAD")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .description("Payment for booking")
                .customerEmail("test@example.com")
                .customerName("John Doe")
                .build();

        assertThat(request.getBookingId()).isEqualTo(100L);
        assertThat(request.getUserId()).isEqualTo(1L);
        assertThat(request.getAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(request.getCurrency()).isEqualTo("MAD");
        assertThat(request.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(request.getDescription()).isEqualTo("Payment for booking");
        assertThat(request.getCustomerEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should have default currency MAD")
    void shouldHaveDefaultCurrencyMAD() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        assertThat(request.getCurrency()).isEqualTo("MAD");
    }

    @Test
    @DisplayName("Should set Stripe specific fields")
    void shouldSetStripeSpecificFields() {
        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .bookingId(100L)
                .userId(1L)
                .amount(new BigDecimal("500.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .stripePaymentMethodId("pm_test123")
                .stripeCustomerId("cus_test123")
                .returnUrl("https://example.com/return")
                .build();

        assertThat(request.getStripePaymentMethodId()).isEqualTo("pm_test123");
        assertThat(request.getStripeCustomerId()).isEqualTo("cus_test123");
        assertThat(request.getReturnUrl()).isEqualTo("https://example.com/return");
    }
}

