package com.code.reservationservice.dao.repository;

import com.code.reservationservice.dao.entity.Reservation;
import com.code.reservationservice.dao.entity.ReservationStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * JPA Specifications for dynamic Reservation queries.
 */
public class ReservationSpecifications {

    public static Specification<Reservation> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) ->
                userId == null ? null : criteriaBuilder.equal(root.get("userId"), userId);
    }

    public static Specification<Reservation> hasRiadId(Long riadId) {
        return (root, query, criteriaBuilder) ->
                riadId == null ? null : criteriaBuilder.equal(root.get("riadId"), riadId);
    }

    public static Specification<Reservation> hasStatus(ReservationStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Reservation> checkInDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) return null;
            if (startDate == null) return criteriaBuilder.lessThanOrEqualTo(root.get("checkInDate"), endDate);
            if (endDate == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("checkInDate"), startDate);
            return criteriaBuilder.between(root.get("checkInDate"), startDate, endDate);
        };
    }

    public static Specification<Reservation> checkOutDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) return null;
            if (startDate == null) return criteriaBuilder.lessThanOrEqualTo(root.get("checkOutDate"), endDate);
            if (endDate == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("checkOutDate"), startDate);
            return criteriaBuilder.between(root.get("checkOutDate"), startDate, endDate);
        };
    }

    public static Specification<Reservation> guestNameContains(String guestName) {
        return (root, query, criteriaBuilder) ->
                guestName == null || guestName.isEmpty() ? null :
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("guestName")),
                                "%" + guestName.toLowerCase() + "%");
    }

    public static Specification<Reservation> hasReservationNumber(String reservationNumber) {
        return (root, query, criteriaBuilder) ->
                reservationNumber == null || reservationNumber.isEmpty() ? null :
                        criteriaBuilder.equal(root.get("reservationNumber"), reservationNumber);
    }
}
