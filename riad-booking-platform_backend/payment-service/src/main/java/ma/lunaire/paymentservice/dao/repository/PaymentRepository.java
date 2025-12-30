package ma.lunaire.paymentservice.dao.repository;

import ma.lunaire.paymentservice.dao.entity.Payment;
import ma.lunaire.paymentservice.dao.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Payment entity with custom query methods.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    /**
     * Find payment by its unique payment number.
     */
    Optional<Payment> findByPaymentNumber(String paymentNumber);

    /**
     * Find payment by transaction ID.
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Find payment by Stripe PaymentIntent ID.
     */
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    /**
     * Find all payments for a specific booking.
     */
    List<Payment> findByBookingId(Long bookingId);

    /**
     * Find all payments for a specific user.
     */
    Page<Payment> findByUserId(Long userId, Pageable pageable);

    /**
     * Find payments by status.
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     * Find payments by user and status.
     */
    Page<Payment> findByUserIdAndStatus(Long userId, PaymentStatus status, Pageable pageable);

    /**
     * Calculate total amount paid for a booking.
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.bookingId = :bookingId AND p.status = 'COMPLETED'")
    BigDecimal getTotalPaidAmountByBookingId(@Param("bookingId") Long bookingId);

    /**
     * Find pending payments older than given date.
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt < :expirationDate")
    List<Payment> findPendingPaymentsOlderThan(@Param("expirationDate") LocalDateTime expirationDate);

    /**
     * Count payments by status.
     */
    long countByStatus(PaymentStatus status);

    /**
     * Find payments within a date range.
     */
    @Query("SELECT p FROM Payment p WHERE p.createdAt >= :startDate AND p.createdAt <= :endDate")
    List<Payment> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Check if booking has any completed payment.
     */
    boolean existsByBookingIdAndStatus(Long bookingId, PaymentStatus status);
}

