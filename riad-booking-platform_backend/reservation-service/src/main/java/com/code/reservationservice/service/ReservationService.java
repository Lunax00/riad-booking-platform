package com.code.reservationservice.service;

import com.code.reservationservice.dao.entity.Reservation;
import com.code.reservationservice.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for reservation operations.
 */
public interface ReservationService {

    /**
     * Create a new reservation.
     */
    ReservationResponse createReservation(CreateReservationRequest request);

    /**
     * Get reservation by ID.
     */
    ReservationResponse getReservationById(Long id);

    /**
     * Get reservation by reservation number.
     */
    ReservationResponse getReservationByNumber(String reservationNumber);

    /**
     * Get all reservations with pagination.
     */
    Page<ReservationResponse> getAllReservations(Pageable pageable);

    /**
     * Get reservations by user ID.
     */
    Page<ReservationResponse> getReservationsByUserId(Long userId, Pageable pageable);

    /**
     * Get reservations by riad ID.
     */
    Page<ReservationResponse> getReservationsByRiadId(Long riadId, Pageable pageable);

    /**
     * Search reservations with criteria.
     */
    Page<ReservationResponse> searchReservations(ReservationSearchCriteria criteria, Pageable pageable);

    /**
     * Update a reservation.
     */
    ReservationResponse updateReservation(Long id, UpdateReservationRequest request);

    /**
     * Confirm a reservation.
     */
    ReservationResponse confirmReservation(Long id);

    /**
     * Cancel a reservation.
     */
    ReservationResponse cancelReservation(Long id, String reason);

    /**
     * Check-in a guest.
     */
    ReservationResponse checkIn(Long id);

    /**
     * Check-out a guest.
     */
    ReservationResponse checkOut(Long id);

    /**
     * Mark reservation as no-show.
     */
    ReservationResponse markNoShow(Long id);

    /**
     * Delete a reservation.
     */
    void deleteReservation(Long id);

    /**
     * Check if riad is available for given dates.
     */
    AvailabilityCheckResponse checkAvailability(AvailabilityCheckRequest request);

    /**
     * Get today's check-ins.
     */
    List<ReservationResponse> getTodayCheckIns();

    /**
     * Get today's check-outs.
     */
    List<ReservationResponse> getTodayCheckOuts();

    /**
     * Update payment information.
     */
    ReservationResponse updatePaymentInfo(Long id, String paymentId);

    /**
     * Expire pending reservations older than specified hours.
     */
    int expirePendingReservations(int hours);
}

