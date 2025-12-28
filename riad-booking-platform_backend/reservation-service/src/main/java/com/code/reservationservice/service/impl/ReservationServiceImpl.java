package com.code.reservationservice.service.impl;

import com.code.reservationservice.dao.entity.Reservation;
import com.code.reservationservice.dao.entity.ReservationStatus;
import com.code.reservationservice.dao.repository.ReservationRepository;
import com.code.reservationservice.dao.repository.ReservationSpecifications;
import com.code.reservationservice.dto.*;
import com.code.reservationservice.exception.InvalidReservationOperationException;
import com.code.reservationservice.exception.ReservationNotFoundException;
import com.code.reservationservice.exception.RiadNotAvailableException;
import com.code.reservationservice.mapper.ReservationMapper;
import com.code.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ReservationService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    @Override
    public ReservationResponse createReservation(CreateReservationRequest request) {
        log.info("Creating reservation for user {} at riad {}", request.getUserId(), request.getRiadId());


        // Validate dates
        validateDates(request.getCheckInDate(), request.getCheckOutDate());

        // Check availability
        if (!reservationRepository.isRiadAvailable(request.getRiadId(),
                request.getCheckInDate(), request.getCheckOutDate())) {
            throw new RiadNotAvailableException(request.getRiadId());
        }

        Reservation reservation = reservationMapper.toEntity(request);
        Reservation savedReservation = reservationRepository.save(reservation);

        log.info("Created reservation with number: {}", savedReservation.getReservationNumber());
        return reservationMapper.toResponse(savedReservation);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = findReservationById(id);
        return reservationMapper.toResponse(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getReservationByNumber(String reservationNumber) {
        Reservation reservation = reservationRepository.findByReservationNumber(reservationNumber)
                .orElseThrow(() -> new ReservationNotFoundException("reservationNumber", reservationNumber));
        return reservationMapper.toResponse(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationResponse> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable)
                .map(reservationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationResponse> getReservationsByUserId(Long userId, Pageable pageable) {
        return reservationRepository.findByUserId(userId, pageable)
                .map(reservationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationResponse> getReservationsByRiadId(Long riadId, Pageable pageable) {
        return reservationRepository.findByRiadId(riadId, pageable)
                .map(reservationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationResponse> searchReservations(ReservationSearchCriteria criteria, Pageable pageable) {
        Specification<Reservation> spec = Specification.where(
                ReservationSpecifications.hasUserId(criteria.getUserId()));

        if (criteria.getRiadId() != null) {
            spec = spec.and(ReservationSpecifications.hasRiadId(criteria.getRiadId()));
        }
        if (criteria.getStatus() != null) {
            spec = spec.and(ReservationSpecifications.hasStatus(criteria.getStatus()));
        }
        if (criteria.getCheckInDateFrom() != null || criteria.getCheckInDateTo() != null) {
            spec = spec.and(ReservationSpecifications.checkInDateBetween(
                    criteria.getCheckInDateFrom(), criteria.getCheckInDateTo()));
        }
        if (criteria.getCheckOutDateFrom() != null || criteria.getCheckOutDateTo() != null) {
            spec = spec.and(ReservationSpecifications.checkOutDateBetween(
                    criteria.getCheckOutDateFrom(), criteria.getCheckOutDateTo()));
        }
        if (criteria.getGuestName() != null && !criteria.getGuestName().isEmpty()) {
            spec = spec.and(ReservationSpecifications.guestNameContains(criteria.getGuestName()));
        }
        if (criteria.getReservationNumber() != null && !criteria.getReservationNumber().isEmpty()) {
            spec = spec.and(ReservationSpecifications.hasReservationNumber(criteria.getReservationNumber()));
        }

        return reservationRepository.findAll(spec, pageable)
                .map(reservationMapper::toResponse);
    }

    @Override
    public ReservationResponse updateReservation(Long id, UpdateReservationRequest request) {
        log.info("Updating reservation with id: {}", id);

        Reservation reservation = findReservationById(id);

        // Only allow updates for PENDING or CONFIRMED reservations
        if (reservation.getStatus() != ReservationStatus.PENDING &&
            reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new InvalidReservationOperationException(
                    "Cannot update reservation with status: " + reservation.getStatus());
        }

        // Update dates if provided and validate
        if (request.getCheckInDate() != null || request.getCheckOutDate() != null) {
            LocalDate newCheckIn = request.getCheckInDate() != null ?
                    request.getCheckInDate() : reservation.getCheckInDate();
            LocalDate newCheckOut = request.getCheckOutDate() != null ?
                    request.getCheckOutDate() : reservation.getCheckOutDate();

            validateDates(newCheckIn, newCheckOut);

            // Check availability for new dates (excluding current reservation)
            List<Reservation> overlapping = reservationRepository.findOverlappingReservations(
                    reservation.getRiadId(), newCheckIn, newCheckOut);
            overlapping.removeIf(r -> r.getId().equals(id));

            if (!overlapping.isEmpty()) {
                throw new RiadNotAvailableException(reservation.getRiadId());
            }
        }

        // Use MapStruct to update non-null fields
        reservationMapper.updateEntityFromRequest(request, reservation);

        Reservation updatedReservation = reservationRepository.save(reservation);
        log.info("Updated reservation with number: {}", updatedReservation.getReservationNumber());
        return reservationMapper.toResponse(updatedReservation);
    }

    @Override
    public ReservationResponse confirmReservation(Long id) {
        log.info("Confirming reservation with id: {}", id);

        Reservation reservation = findReservationById(id);

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidReservationOperationException(
                    "Can only confirm PENDING reservations. Current status: " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        Reservation confirmedReservation = reservationRepository.save(reservation);

        log.info("Confirmed reservation with number: {}", confirmedReservation.getReservationNumber());
        return reservationMapper.toResponse(confirmedReservation);
    }

    @Override
    public ReservationResponse cancelReservation(Long id, String reason) {
        log.info("Cancelling reservation with id: {}", id);

        Reservation reservation = findReservationById(id);

        if (reservation.getStatus() == ReservationStatus.CANCELLED ||
            reservation.getStatus() == ReservationStatus.CHECKED_OUT) {
            throw new InvalidReservationOperationException(
                    "Cannot cancel reservation with status: " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancellationReason(reason);
        Reservation cancelledReservation = reservationRepository.save(reservation);

        log.info("Cancelled reservation with number: {}", cancelledReservation.getReservationNumber());
        return reservationMapper.toResponse(cancelledReservation);
    }

    @Override
    public ReservationResponse checkIn(Long id) {
        log.info("Checking in reservation with id: {}", id);

        Reservation reservation = findReservationById(id);

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new InvalidReservationOperationException(
                    "Can only check-in CONFIRMED reservations. Current status: " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.CHECKED_IN);
        Reservation checkedInReservation = reservationRepository.save(reservation);

        log.info("Checked in reservation with number: {}", checkedInReservation.getReservationNumber());
        return reservationMapper.toResponse(checkedInReservation);
    }

    @Override
    public ReservationResponse checkOut(Long id) {
        log.info("Checking out reservation with id: {}", id);

        Reservation reservation = findReservationById(id);

        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new InvalidReservationOperationException(
                    "Can only check-out CHECKED_IN reservations. Current status: " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        Reservation checkedOutReservation = reservationRepository.save(reservation);

        log.info("Checked out reservation with number: {}", checkedOutReservation.getReservationNumber());
        return reservationMapper.toResponse(checkedOutReservation);
    }

    @Override
    public ReservationResponse markNoShow(Long id) {
        log.info("Marking reservation as no-show with id: {}", id);

        Reservation reservation = findReservationById(id);

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new InvalidReservationOperationException(
                    "Can only mark CONFIRMED reservations as no-show. Current status: " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.NO_SHOW);
        Reservation noShowReservation = reservationRepository.save(reservation);

        log.info("Marked reservation as no-show with number: {}", noShowReservation.getReservationNumber());
        return reservationMapper.toResponse(noShowReservation);
    }

    @Override
    public void deleteReservation(Long id) {
        log.info("Deleting reservation with id: {}", id);

        Reservation reservation = findReservationById(id);
        reservationRepository.delete(reservation);

        log.info("Deleted reservation with number: {}", reservation.getReservationNumber());
    }

    @Override
    @Transactional(readOnly = true)
    public AvailabilityCheckResponse checkAvailability(AvailabilityCheckRequest request) {
        validateDates(request.getCheckInDate(), request.getCheckOutDate());

        boolean available = reservationRepository.isRiadAvailable(
                request.getRiadId(), request.getCheckInDate(), request.getCheckOutDate());

        return AvailabilityCheckResponse.builder()
                .riadId(request.getRiadId())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .available(available)
                .message(available ? "Riad is available for the selected dates" :
                        "Riad is not available for the selected dates")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getTodayCheckIns() {
        LocalDate today = LocalDate.now();
        return reservationRepository.findByCheckInDateAndStatus(today, ReservationStatus.CONFIRMED)
                .stream()
                .map(reservationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getTodayCheckOuts() {
        LocalDate today = LocalDate.now();
        return reservationRepository.findByCheckOutDateAndStatus(today, ReservationStatus.CHECKED_IN)
                .stream()
                .map(reservationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationResponse updatePaymentInfo(Long id, String paymentId) {
        log.info("Updating payment info for reservation with id: {}", id);

        Reservation reservation = findReservationById(id);
        reservation.setPaymentId(paymentId);
        Reservation updatedReservation = reservationRepository.save(reservation);

        log.info("Updated payment info for reservation with number: {}", updatedReservation.getReservationNumber());
        return reservationMapper.toResponse(updatedReservation);
    }

    @Override
    public int expirePendingReservations(int hours) {
        log.info("Expiring pending reservations older than {} hours", hours);

        LocalDateTime expirationDate = LocalDateTime.now().minusHours(hours);
        List<Reservation> expiredReservations = reservationRepository.findPendingReservationsOlderThan(expirationDate);

        for (Reservation reservation : expiredReservations) {
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservationRepository.save(reservation);
            log.info("Expired reservation with number: {}", reservation.getReservationNumber());
        }

        return expiredReservations.size();
    }

    // Helper methods

    private Reservation findReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }

    private void validateDates(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }
        if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
    }
}

