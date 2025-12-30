package com.code.reservationservice.dao.repository;

import com.code.reservationservice.dao.entity.Reservation;
import com.code.reservationservice.dao.entity.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReservationSpecifications.
 */
class ReservationSpecificationsTest {

    private Root<Reservation> root;
    private CriteriaQuery<?> query;
    private CriteriaBuilder criteriaBuilder;
    private Path<Object> path;
    private Predicate predicate;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        root = mock(Root.class);
        query = mock(CriteriaQuery.class);
        criteriaBuilder = mock(CriteriaBuilder.class);
        path = mock(Path.class);
        predicate = mock(Predicate.class);
    }

    @Nested
    @DisplayName("hasUserId Specification")
    class HasUserIdSpecification {

        @Test
        @DisplayName("Should return null predicate when userId is null")
        void shouldReturnNullWhenUserIdIsNull() {
            Specification<Reservation> spec = ReservationSpecifications.hasUserId(null);

            Predicate result = spec.toPredicate(root, query, criteriaBuilder);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should create equal predicate when userId is provided")
        void shouldCreateEqualPredicateWhenUserIdProvided() {
            when(root.get("userId")).thenReturn(path);
            when(criteriaBuilder.equal(path, 1L)).thenReturn(predicate);

            Specification<Reservation> spec = ReservationSpecifications.hasUserId(1L);
            Predicate result = spec.toPredicate(root, query, criteriaBuilder);

            assertThat(result).isNotNull();
            verify(criteriaBuilder).equal(path, 1L);
        }
    }

    @Nested
    @DisplayName("hasRiadId Specification")
    class HasRiadIdSpecification {

        @Test
        @DisplayName("Should return null predicate when riadId is null")
        void shouldReturnNullWhenRiadIdIsNull() {
            Specification<Reservation> spec = ReservationSpecifications.hasRiadId(null);

            Predicate result = spec.toPredicate(root, query, criteriaBuilder);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should create equal predicate when riadId is provided")
        void shouldCreateEqualPredicateWhenRiadIdProvided() {
            when(root.get("riadId")).thenReturn(path);
            when(criteriaBuilder.equal(path, 100L)).thenReturn(predicate);

            Specification<Reservation> spec = ReservationSpecifications.hasRiadId(100L);
            Predicate result = spec.toPredicate(root, query, criteriaBuilder);

            assertThat(result).isNotNull();
            verify(criteriaBuilder).equal(path, 100L);
        }
    }

    @Nested
    @DisplayName("hasStatus Specification")
    class HasStatusSpecification {

        @Test
        @DisplayName("Should return null predicate when status is null")
        void shouldReturnNullWhenStatusIsNull() {
            Specification<Reservation> spec = ReservationSpecifications.hasStatus(null);

            Predicate result = spec.toPredicate(root, query, criteriaBuilder);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should create equal predicate when status is provided")
        void shouldCreateEqualPredicateWhenStatusProvided() {
            when(root.get("status")).thenReturn(path);
            when(criteriaBuilder.equal(path, ReservationStatus.CONFIRMED)).thenReturn(predicate);

            Specification<Reservation> spec = ReservationSpecifications.hasStatus(ReservationStatus.CONFIRMED);
            Predicate result = spec.toPredicate(root, query, criteriaBuilder);

            assertThat(result).isNotNull();
            verify(criteriaBuilder).equal(path, ReservationStatus.CONFIRMED);
        }
    }

    @Nested
    @DisplayName("checkInDateBetween Specification")
    class CheckInDateBetweenSpecification {

        @Test
        @DisplayName("Should return null when both dates are null")
        void shouldReturnNullWhenBothDatesAreNull() {
            Specification<Reservation> spec = ReservationSpecifications.checkInDateBetween(null, null);

            Predicate result = spec.toPredicate(root, query, criteriaBuilder);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should create lessThanOrEqualTo when only endDate is provided")
        @SuppressWarnings("unchecked")
        void shouldCreateLessThanOrEqualToWhenOnlyEndDateProvided() {
            Path<LocalDate> datePath = mock(Path.class);
            LocalDate endDate = LocalDate.now().plusDays(5);
            when(root.<LocalDate>get("checkInDate")).thenReturn(datePath);
            when(criteriaBuilder.lessThanOrEqualTo(datePath, endDate)).thenReturn(predicate);

            Specification<Reservation> spec = ReservationSpecifications.checkInDateBetween(null, endDate);
            Predicate result = spec.toPredicate(root, query, criteriaBuilder);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should create greaterThanOrEqualTo when only startDate is provided")
        @SuppressWarnings("unchecked")
        void shouldCreateGreaterThanOrEqualToWhenOnlyStartDateProvided() {
            Path<LocalDate> datePath = mock(Path.class);
            LocalDate startDate = LocalDate.now();
            when(root.<LocalDate>get("checkInDate")).thenReturn(datePath);
            when(criteriaBuilder.greaterThanOrEqualTo(datePath, startDate)).thenReturn(predicate);

            Specification<Reservation> spec = ReservationSpecifications.checkInDateBetween(startDate, null);
            Predicate result = spec.toPredicate(root, query, criteriaBuilder);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should create between when both dates are provided")
        @SuppressWarnings("unchecked")
        void shouldCreateBetweenWhenBothDatesProvided() {
            Path<LocalDate> datePath = mock(Path.class);
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = LocalDate.now().plusDays(5);
            when(root.<LocalDate>get("checkInDate")).thenReturn(datePath);
            when(criteriaBuilder.between(datePath, startDate, endDate)).thenReturn(predicate);

            Specification<Reservation> spec = ReservationSpecifications.checkInDateBetween(startDate, endDate);
            Predicate result = spec.toPredicate(root, query, criteriaBuilder);

            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("checkOutDateBetween Specification")
    class CheckOutDateBetweenSpecification {

        @Test
        @DisplayName("Should return null when both dates are null")
        void shouldReturnNullWhenBothDatesAreNull() {
            Specification<Reservation> spec = ReservationSpecifications.checkOutDateBetween(null, null);

            Predicate result = spec.toPredicate(root, query, criteriaBuilder);

            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("guestNameContains Specification")
    class GuestNameContainsSpecification {

        @Test
        @DisplayName("Should return null when guestName is null")
        void shouldReturnNullWhenGuestNameIsNull() {
            Specification<Reservation> spec = ReservationSpecifications.guestNameContains(null);

            Predicate result = spec.toPredicate(root, query, criteriaBuilder);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should return null when guestName is empty")
        void shouldReturnNullWhenGuestNameIsEmpty() {
            Specification<Reservation> spec = ReservationSpecifications.guestNameContains("");

            Predicate result = spec.toPredicate(root, query, criteriaBuilder);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should create like predicate when guestName is provided")
        @SuppressWarnings("unchecked")
        void shouldCreateLikePredicateWhenGuestNameProvided() {
            Path<String> namePath = mock(Path.class);
            Expression<String> lowerExpression = mock(Expression.class);
            when(root.<String>get("guestName")).thenReturn(namePath);
            when(criteriaBuilder.lower(namePath)).thenReturn(lowerExpression);
            when(criteriaBuilder.like(lowerExpression, "%john%")).thenReturn(predicate);

            Specification<Reservation> spec = ReservationSpecifications.guestNameContains("John");
            Predicate result = spec.toPredicate(root, query, criteriaBuilder);

            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("hasReservationNumber Specification")
    class HasReservationNumberSpecification {

        @Test
        @DisplayName("Should return null when reservationNumber is null")
        void shouldReturnNullWhenReservationNumberIsNull() {
            Specification<Reservation> spec = ReservationSpecifications.hasReservationNumber(null);

            Predicate result = spec.toPredicate(root, query, criteriaBuilder);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should return null when reservationNumber is empty")
        void shouldReturnNullWhenReservationNumberIsEmpty() {
            Specification<Reservation> spec = ReservationSpecifications.hasReservationNumber("");

            Predicate result = spec.toPredicate(root, query, criteriaBuilder);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should create equal predicate when reservationNumber is provided")
        void shouldCreateEqualPredicateWhenReservationNumberProvided() {
            when(root.get("reservationNumber")).thenReturn(path);
            when(criteriaBuilder.equal(path, "RES-12345678")).thenReturn(predicate);

            Specification<Reservation> spec = ReservationSpecifications.hasReservationNumber("RES-12345678");
            Predicate result = spec.toPredicate(root, query, criteriaBuilder);

            assertThat(result).isNotNull();
            verify(criteriaBuilder).equal(path, "RES-12345678");
        }
    }
}

