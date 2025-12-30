package ma.lunaire.paymentservice.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for RefundRequest DTO.
 */
class RefundRequestTest {

    @Test
    @DisplayName("Should create request with builder")
    void shouldCreateRequestWithBuilder() {
        RefundRequest request = RefundRequest.builder()
                .amount(new BigDecimal("100.00"))
                .reason("Customer requested refund")
                .build();

        assertThat(request.getAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(request.getReason()).isEqualTo("Customer requested refund");
    }

    @Test
    @DisplayName("Should create request with no-args constructor")
    void shouldCreateRequestWithNoArgsConstructor() {
        RefundRequest request = new RefundRequest();
        request.setAmount(new BigDecimal("50.00"));
        request.setReason("Test refund");

        assertThat(request.getAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(request.getReason()).isEqualTo("Test refund");
    }
}

