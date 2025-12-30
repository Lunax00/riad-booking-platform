package ma.lunaire.paymentservice.dao.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for PaymentStatus enum.
 */
class PaymentStatusTest {

    @Test
    @DisplayName("Should have all expected payment statuses")
    void shouldHaveAllExpectedStatuses() {
        PaymentStatus[] statuses = PaymentStatus.values();
        assertThat(statuses).hasSize(7);
    }

    @Test
    @DisplayName("Should contain PENDING status")
    void shouldContainPendingStatus() {
        assertThat(PaymentStatus.valueOf("PENDING")).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    @DisplayName("Should contain PROCESSING status")
    void shouldContainProcessingStatus() {
        assertThat(PaymentStatus.valueOf("PROCESSING")).isEqualTo(PaymentStatus.PROCESSING);
    }

    @Test
    @DisplayName("Should contain COMPLETED status")
    void shouldContainCompletedStatus() {
        assertThat(PaymentStatus.valueOf("COMPLETED")).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should contain FAILED status")
    void shouldContainFailedStatus() {
        assertThat(PaymentStatus.valueOf("FAILED")).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    @DisplayName("Should contain REFUNDED status")
    void shouldContainRefundedStatus() {
        assertThat(PaymentStatus.valueOf("REFUNDED")).isEqualTo(PaymentStatus.REFUNDED);
    }

    @Test
    @DisplayName("Should contain PARTIALLY_REFUNDED status")
    void shouldContainPartiallyRefundedStatus() {
        assertThat(PaymentStatus.valueOf("PARTIALLY_REFUNDED")).isEqualTo(PaymentStatus.PARTIALLY_REFUNDED);
    }

    @Test
    @DisplayName("Should contain CANCELLED status")
    void shouldContainCancelledStatus() {
        assertThat(PaymentStatus.valueOf("CANCELLED")).isEqualTo(PaymentStatus.CANCELLED);
    }
}

