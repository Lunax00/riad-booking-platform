package ma.lunaire.paymentservice.dto;

import ma.lunaire.paymentservice.dao.entity.PaymentMethod;
import ma.lunaire.paymentservice.dao.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for searching payments with various criteria.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSearchCriteria {

    private Long userId;
    private Long bookingId;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
    private String paymentNumber;
    private String transactionId;
}

