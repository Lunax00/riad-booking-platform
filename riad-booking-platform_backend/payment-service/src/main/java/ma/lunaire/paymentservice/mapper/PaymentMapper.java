package ma.lunaire.paymentservice.mapper;

import ma.lunaire.paymentservice.dao.entity.Payment;
import ma.lunaire.paymentservice.dto.CreatePaymentRequest;
import ma.lunaire.paymentservice.dto.PaymentResponse;
import org.mapstruct.*;

/**
 * MapStruct mapper for converting between Payment entity and DTOs.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PaymentMapper {

    /**
     * Convert CreatePaymentRequest to Payment entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "stripePaymentIntentId", ignore = true)
    @Mapping(target = "stripeClientSecret", ignore = true)
    @Mapping(target = "stripeReceiptUrl", ignore = true)
    @Mapping(target = "stripeRefundId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "paidAt", ignore = true)
    @Mapping(target = "refundedAt", ignore = true)
    @Mapping(target = "refundedAmount", ignore = true)
    @Mapping(target = "failureReason", ignore = true)
    @Mapping(target = "gatewayResponse", ignore = true)
    @Mapping(target = "cardLastFour", ignore = true)
    @Mapping(target = "cardBrand", ignore = true)
    Payment toEntity(CreatePaymentRequest request);

    /**
     * Convert Payment entity to PaymentResponse DTO.
     */
    @Mapping(target = "clientSecret", source = "stripeClientSecret")
    @Mapping(target = "receiptUrl", source = "stripeReceiptUrl")
    @Mapping(target = "requiresAction", ignore = true)
    @Mapping(target = "nextActionUrl", ignore = true)
    PaymentResponse toResponse(Payment payment);
}
