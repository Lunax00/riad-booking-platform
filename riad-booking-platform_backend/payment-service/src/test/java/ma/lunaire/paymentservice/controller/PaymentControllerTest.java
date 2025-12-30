package ma.lunaire.paymentservice.controller;

import ma.lunaire.paymentservice.dao.entity.PaymentMethod;
import ma.lunaire.paymentservice.dao.entity.PaymentStatus;
import ma.lunaire.paymentservice.dto.CreatePaymentRequest;
import ma.lunaire.paymentservice.dto.PaymentResponse;
import ma.lunaire.paymentservice.dto.RefundRequest;
import ma.lunaire.paymentservice.service.PaymentService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PaymentController.
 */
@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private PaymentResponse paymentResponse;
    private CreatePaymentRequest createRequest;

    @BeforeEach
    void setUp() {
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
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();
    }

    @Nested
    @DisplayName("Create Payment Tests")
    class CreatePaymentTests {

        @Test
        @DisplayName("Should create payment and return CREATED status")
        void shouldCreatePaymentAndReturnCreatedStatus() {
            when(paymentService.createPayment(any(CreatePaymentRequest.class))).thenReturn(paymentResponse);

            ResponseEntity<PaymentResponse> response = paymentController.createPayment(createRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getPaymentNumber()).isEqualTo("PAY-12345678");
            verify(paymentService).createPayment(createRequest);
        }
    }

    @Nested
    @DisplayName("Get Payment Tests")
    class GetPaymentTests {

        @Test
        @DisplayName("Should get payment by ID")
        void shouldGetPaymentById() {
            when(paymentService.getPaymentById(1L)).thenReturn(paymentResponse);

            ResponseEntity<PaymentResponse> response = paymentController.getPaymentById(1L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should get payment by payment number")
        void shouldGetPaymentByPaymentNumber() {
            when(paymentService.getPaymentByNumber("PAY-12345678")).thenReturn(paymentResponse);

            ResponseEntity<PaymentResponse> response = paymentController.getPaymentByNumber("PAY-12345678");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
        }

        @Test
        @DisplayName("Should get all payments with pagination")
        void shouldGetAllPaymentsWithPagination() {
            Page<PaymentResponse> page = new PageImpl<>(List.of(paymentResponse));
            Pageable pageable = PageRequest.of(0, 10);
            when(paymentService.getAllPayments(any(Pageable.class))).thenReturn(page);

            ResponseEntity<Page<PaymentResponse>> response = paymentController.getAllPayments(pageable);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getContent()).hasSize(1);
        }

        @Test
        @DisplayName("Should get payments by user ID")
        void shouldGetPaymentsByUserId() {
            Page<PaymentResponse> page = new PageImpl<>(List.of(paymentResponse));
            Pageable pageable = PageRequest.of(0, 10);
            when(paymentService.getPaymentsByUserId(eq(1L), any(Pageable.class))).thenReturn(page);

            ResponseEntity<Page<PaymentResponse>> response = paymentController.getPaymentsByUserId(1L, pageable);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getContent()).hasSize(1);
        }

        @Test
        @DisplayName("Should get payments by booking ID")
        void shouldGetPaymentsByBookingId() {
            when(paymentService.getPaymentsByBookingId(100L)).thenReturn(List.of(paymentResponse));

            ResponseEntity<List<PaymentResponse>> response = paymentController.getPaymentsByBookingId(100L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Payment Operations Tests")
    class PaymentOperationsTests {

        @Test
        @DisplayName("Should confirm payment")
        void shouldConfirmPayment() {
            PaymentResponse confirmedResponse = PaymentResponse.builder()
                    .id(1L)
                    .status(PaymentStatus.COMPLETED)
                    .build();

            when(paymentService.confirmPayment(1L)).thenReturn(confirmedResponse);

            ResponseEntity<PaymentResponse> response = paymentController.confirmPayment(1L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        }

        @Test
        @DisplayName("Should cancel payment")
        void shouldCancelPayment() {
            PaymentResponse cancelledResponse = PaymentResponse.builder()
                    .id(1L)
                    .status(PaymentStatus.CANCELLED)
                    .build();

            when(paymentService.cancelPayment(1L, "User request")).thenReturn(cancelledResponse);

            ResponseEntity<PaymentResponse> response = paymentController.cancelPayment(1L, "User request");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getStatus()).isEqualTo(PaymentStatus.CANCELLED);
        }

        @Test
        @DisplayName("Should refund payment")
        void shouldRefundPayment() {
            RefundRequest refundRequest = RefundRequest.builder()
                    .amount(new BigDecimal("100.00"))
                    .reason("Customer request")
                    .build();

            PaymentResponse refundedResponse = PaymentResponse.builder()
                    .id(1L)
                    .status(PaymentStatus.PARTIALLY_REFUNDED)
                    .refundedAmount(new BigDecimal("100.00"))
                    .build();

            when(paymentService.refundPayment(1L, refundRequest)).thenReturn(refundedResponse);

            ResponseEntity<PaymentResponse> response = paymentController.refundPayment(1L, refundRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getStatus()).isEqualTo(PaymentStatus.PARTIALLY_REFUNDED);
        }

        @Test
        @DisplayName("Should sync payment status")
        void shouldSyncPaymentStatus() {
            PaymentResponse syncedResponse = PaymentResponse.builder()
                    .id(1L)
                    .status(PaymentStatus.COMPLETED)
                    .build();

            when(paymentService.syncPaymentStatus(1L)).thenReturn(syncedResponse);

            ResponseEntity<PaymentResponse> response = paymentController.syncPaymentStatus(1L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        }
    }

    @Nested
    @DisplayName("Utility Endpoint Tests")
    class UtilityEndpointTests {

        @Test
        @DisplayName("Should get total paid amount")
        void shouldGetTotalPaidAmount() {
            when(paymentService.getTotalPaidAmountByBookingId(100L)).thenReturn(new BigDecimal("1000.00"));

            ResponseEntity<BigDecimal> response = paymentController.getTotalPaidAmount(100L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualByComparingTo(new BigDecimal("1000.00"));
        }

        @Test
        @DisplayName("Should check if booking has completed payment")
        void shouldCheckIfBookingHasCompletedPayment() {
            when(paymentService.hasCompletedPayment(100L)).thenReturn(true);

            ResponseEntity<Boolean> response = paymentController.hasCompletedPayment(100L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isTrue();
        }
    }
}

