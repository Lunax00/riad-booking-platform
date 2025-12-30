package ma.lunaire.paymentservice.service;

import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import ma.lunaire.paymentservice.config.StripeConfig;
import ma.lunaire.paymentservice.dao.entity.Payment;
import ma.lunaire.paymentservice.dao.entity.PaymentMethod;
import ma.lunaire.paymentservice.dao.entity.PaymentStatus;
import ma.lunaire.paymentservice.dao.repository.PaymentRepository;
import ma.lunaire.paymentservice.dto.CreatePaymentRequest;
import ma.lunaire.paymentservice.dto.PaymentResponse;
import ma.lunaire.paymentservice.dto.RefundRequest;
import ma.lunaire.paymentservice.exception.InvalidPaymentOperationException;
import ma.lunaire.paymentservice.exception.PaymentNotFoundException;
import ma.lunaire.paymentservice.mapper.PaymentMapper;
import ma.lunaire.paymentservice.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PaymentServiceImpl with Stripe integration.
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private StripeService stripeService;

    @Mock
    private StripeConfig stripeConfig;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;
    private PaymentResponse paymentResponse;
    private CreatePaymentRequest createRequest;

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
                .stripePaymentIntentId("pi_test123")
                .build();

        paymentResponse = PaymentResponse.builder()
                .id(1L)
                .paymentNumber("PAY-12345678")
                .bookingId(100L)
                .userId(1L)
                .amount(new BigDecimal("500.00"))
                .currency("MAD")
                .status(PaymentStatus.PENDING)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        createRequest = CreatePaymentRequest.builder()
                .bookingId(100L)
                .userId(1L)
                .amount(new BigDecimal("500.00"))
                .currency("MAD")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .customerEmail("test@example.com")
                .build();
    }

    private PaymentIntent createMockPaymentIntent(String status) {
        PaymentIntent intent = mock(PaymentIntent.class);
        lenient().when(intent.getId()).thenReturn("pi_test123");
        lenient().when(intent.getClientSecret()).thenReturn("pi_test123_secret");
        lenient().when(intent.getStatus()).thenReturn(status);
        lenient().when(intent.getLatestCharge()).thenReturn(null);
        return intent;
    }

    @Nested
    @DisplayName("Create Payment Tests")
    class CreatePaymentTests {

        @Test
        @DisplayName("Should create payment with Stripe successfully")
        void shouldCreatePaymentWithStripeSuccessfully() {
            Customer mockCustomer = mock(Customer.class);
            when(mockCustomer.getId()).thenReturn("cus_test123");
            PaymentIntent mockIntent = createMockPaymentIntent("requires_payment_method");

            when(stripeService.createOrRetrieveCustomer(anyString(), any(), anyMap())).thenReturn(mockCustomer);
            when(stripeService.convertToStripeAmount(any(BigDecimal.class))).thenReturn(50000L);
            when(stripeService.createPaymentIntent(anyLong(), anyString(), anyString(), any(), anyString(), anyMap()))
                    .thenReturn(mockIntent);
            when(paymentMapper.toEntity(any(CreatePaymentRequest.class))).thenReturn(payment);
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
            when(paymentMapper.toResponse(any(Payment.class))).thenReturn(paymentResponse);

            PaymentResponse result = paymentService.createPayment(createRequest);

            assertThat(result).isNotNull();
            assertThat(result.getBookingId()).isEqualTo(100L);
            verify(stripeService).createPaymentIntent(anyLong(), anyString(), anyString(), any(), anyString(), anyMap());
            verify(paymentRepository).save(any(Payment.class));
        }
    }

    @Nested
    @DisplayName("Get Payment Tests")
    class GetPaymentTests {

        @Test
        @DisplayName("Should get payment by ID")
        void shouldGetPaymentById() {
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
            when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);

            PaymentResponse result = paymentService.getPaymentById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw exception when payment not found")
        void shouldThrowExceptionWhenPaymentNotFound() {
            when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> paymentService.getPaymentById(999L))
                    .isInstanceOf(PaymentNotFoundException.class);
        }

        @Test
        @DisplayName("Should get payment by payment number")
        void shouldGetPaymentByPaymentNumber() {
            when(paymentRepository.findByPaymentNumber("PAY-12345678")).thenReturn(Optional.of(payment));
            when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);

            PaymentResponse result = paymentService.getPaymentByNumber("PAY-12345678");

            assertThat(result).isNotNull();
            assertThat(result.getPaymentNumber()).isEqualTo("PAY-12345678");
        }

        @Test
        @DisplayName("Should get payments by booking ID")
        void shouldGetPaymentsByBookingId() {
            when(paymentRepository.findByBookingId(100L)).thenReturn(List.of(payment));
            when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);

            List<PaymentResponse> results = paymentService.getPaymentsByBookingId(100L);

            assertThat(results).hasSize(1);
            assertThat(results.get(0).getBookingId()).isEqualTo(100L);
        }
    }

    @Nested
    @DisplayName("Confirm Payment Tests")
    class ConfirmPaymentTests {

        @Test
        @DisplayName("Should confirm payment via Stripe successfully")
        void shouldConfirmPaymentViaStripeSuccessfully() {
            PaymentIntent confirmedIntent = mock(PaymentIntent.class);
            when(confirmedIntent.getId()).thenReturn("pi_test123");
            when(confirmedIntent.getClientSecret()).thenReturn("pi_test123_secret");
            when(confirmedIntent.getStatus()).thenReturn("succeeded");
            when(confirmedIntent.getLatestCharge()).thenReturn(null);

            PaymentResponse confirmedResponse = PaymentResponse.builder()
                    .id(1L)
                    .status(PaymentStatus.COMPLETED)
                    .build();

            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
            when(stripeService.confirmPaymentIntent(anyString(), any())).thenReturn(confirmedIntent);
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
            when(paymentMapper.toResponse(any(Payment.class))).thenReturn(confirmedResponse);

            PaymentResponse result = paymentService.confirmPayment(1L);

            assertThat(result.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
            verify(stripeService).confirmPaymentIntent(eq("pi_test123"), any());
        }

        @Test
        @DisplayName("Should throw exception when payment has no Stripe PaymentIntent")
        void shouldThrowExceptionWhenNoStripePaymentIntent() {
            payment.setStripePaymentIntentId(null);
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

            assertThatThrownBy(() -> paymentService.confirmPayment(1L))
                    .isInstanceOf(InvalidPaymentOperationException.class)
                    .hasMessageContaining("no associated Stripe PaymentIntent");
        }
    }

    @Nested
    @DisplayName("Cancel Payment Tests")
    class CancelPaymentTests {

        @Test
        @DisplayName("Should cancel pending payment via Stripe")
        void shouldCancelPendingPaymentViaStripe() {
            PaymentIntent cancelledIntent = createMockPaymentIntent("canceled");
            PaymentResponse cancelledResponse = PaymentResponse.builder()
                    .id(1L)
                    .status(PaymentStatus.CANCELLED)
                    .build();

            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
            when(stripeService.cancelPaymentIntent(anyString())).thenReturn(cancelledIntent);
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
            when(paymentMapper.toResponse(any(Payment.class))).thenReturn(cancelledResponse);

            PaymentResponse result = paymentService.cancelPayment(1L, "User cancelled");

            assertThat(result.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
            verify(stripeService).cancelPaymentIntent("pi_test123");
        }

        @Test
        @DisplayName("Should throw exception when cancelling non-pending payment")
        void shouldThrowExceptionWhenCancellingNonPendingPayment() {
            payment.setStatus(PaymentStatus.COMPLETED);
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

            assertThatThrownBy(() -> paymentService.cancelPayment(1L, "User cancelled"))
                    .isInstanceOf(InvalidPaymentOperationException.class)
                    .hasMessageContaining("Can only cancel PENDING payments");
        }
    }

    @Nested
    @DisplayName("Refund Payment Tests")
    class RefundPaymentTests {

        @Test
        @DisplayName("Should refund completed payment via Stripe")
        void shouldRefundCompletedPaymentViaStripe() {
            payment.setStatus(PaymentStatus.COMPLETED);

            RefundRequest refundRequest = RefundRequest.builder()
                    .amount(new BigDecimal("500.00"))
                    .reason("Customer request")
                    .build();

            Refund mockRefund = mock(Refund.class);
            when(mockRefund.getId()).thenReturn("re_test123");

            PaymentResponse refundedResponse = PaymentResponse.builder()
                    .id(1L)
                    .status(PaymentStatus.REFUNDED)
                    .refundedAmount(new BigDecimal("500.00"))
                    .build();

            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
            when(stripeService.convertToStripeAmount(any(BigDecimal.class))).thenReturn(50000L);
            when(stripeService.createRefund(anyString(), anyLong(), anyString())).thenReturn(mockRefund);
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
            when(paymentMapper.toResponse(any(Payment.class))).thenReturn(refundedResponse);

            PaymentResponse result = paymentService.refundPayment(1L, refundRequest);

            assertThat(result.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
            verify(stripeService).createRefund(eq("pi_test123"), eq(50000L), eq("Customer request"));
        }

        @Test
        @DisplayName("Should throw exception when refunding non-completed payment")
        void shouldThrowExceptionWhenRefundingNonCompletedPayment() {
            RefundRequest refundRequest = RefundRequest.builder()
                    .amount(new BigDecimal("100.00"))
                    .build();

            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

            assertThatThrownBy(() -> paymentService.refundPayment(1L, refundRequest))
                    .isInstanceOf(InvalidPaymentOperationException.class)
                    .hasMessageContaining("Can only refund COMPLETED payments");
        }

        @Test
        @DisplayName("Should throw exception when refund exceeds payment amount")
        void shouldThrowExceptionWhenRefundExceedsPaymentAmount() {
            payment.setStatus(PaymentStatus.COMPLETED);

            RefundRequest refundRequest = RefundRequest.builder()
                    .amount(new BigDecimal("1000.00"))
                    .build();

            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

            assertThatThrownBy(() -> paymentService.refundPayment(1L, refundRequest))
                    .isInstanceOf(InvalidPaymentOperationException.class)
                    .hasMessageContaining("Total refund amount cannot exceed payment amount");
        }
    }

    @Nested
    @DisplayName("Utility Method Tests")
    class UtilityMethodTests {

        @Test
        @DisplayName("Should get total paid amount by booking ID")
        void shouldGetTotalPaidAmountByBookingId() {
            when(paymentRepository.getTotalPaidAmountByBookingId(100L))
                    .thenReturn(new BigDecimal("1000.00"));

            BigDecimal total = paymentService.getTotalPaidAmountByBookingId(100L);

            assertThat(total).isEqualByComparingTo(new BigDecimal("1000.00"));
        }

        @Test
        @DisplayName("Should check if booking has completed payment")
        void shouldCheckIfBookingHasCompletedPayment() {
            when(paymentRepository.existsByBookingIdAndStatus(100L, PaymentStatus.COMPLETED))
                    .thenReturn(true);

            boolean hasPayment = paymentService.hasCompletedPayment(100L);

            assertThat(hasPayment).isTrue();
        }
    }
}

