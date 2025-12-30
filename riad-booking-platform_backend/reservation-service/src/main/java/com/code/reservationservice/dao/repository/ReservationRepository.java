package com.code.reservationservice.dao.repository;

import com.code.reservationservice.dao.entity.Reservation;
import com.code.reservationservice.dao.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Reservation entity with custom query methods.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    /**
     * Find reservation by its unique reservation number.
     */
    Optional<Reservation> findByReservationNumber(String reservationNumber);

    /**
     * Find all reservations for a specific user.
     */
    Page<Reservation> findByUserId(Long userId, Pageable pageable);

    /**
     * Find all reservations for a specific riad.
     */
    Page<Reservation> findByRiadId(Long riadId, Pageable pageable);

    /**
     * Find reservations by status.
     */
    List<Reservation> findByStatus(ReservationStatus status);

    /**
     * Find reservations for a riad within a date range (to check availability).
     */
    @Query("SELECT r FROM Reservation r WHERE r.riadId = :riadId " +
           "AND r.status NOT IN ('CANCELLED', 'EXPIRED', 'NO_SHOW') " +
           "AND ((r.checkInDate <= :checkOutDate AND r.checkOutDate >= :checkInDate))")
    List<Reservation> findOverlappingReservations(
            @Param("riadId") Long riadId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    /**
     * Find all reservations with check-in date today.
     */
    List<Reservation> findByCheckInDateAndStatus(LocalDate checkInDate, ReservationStatus status);

    /**
     * Find all reservations with check-out date today.
     */
    List<Reservation> findByCheckOutDateAndStatus(LocalDate checkOutDate, ReservationStatus status);

    /**
     * Find pending reservations that are older than given date (for expiration).
     */
    @Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING' AND r.createdAt < :expirationDate")
    List<Reservation> findPendingReservationsOlderThan(@Param("expirationDate") java.time.LocalDateTime expirationDate);

    /**
     * Count reservations by user and status.
     */
    long countByUserIdAndStatus(Long userId, ReservationStatus status);

    /**
     * Find reservations for a user within a date range.
     */
    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId " +
           "AND r.checkInDate >= :startDate AND r.checkInDate <= :endDate")
    List<Reservation> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Check if riad is available for the given dates.
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN false ELSE true END FROM Reservation r " +
           "WHERE r.riadId = :riadId " +
           "AND r.status NOT IN ('CANCELLED', 'EXPIRED', 'NO_SHOW') " +
           "AND ((r.checkInDate <= :checkOutDate AND r.checkOutDate >= :checkInDate))")
    boolean isRiadAvailable(
            @Param("riadId") Long riadId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );
}

