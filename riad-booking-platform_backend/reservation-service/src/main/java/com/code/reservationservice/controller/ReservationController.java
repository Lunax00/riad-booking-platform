package com.code.reservationservice.controller;

import com.code.reservationservice.dto.*;
import com.code.reservationservice.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for reservation operations.
 */
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Create a new reservation.
     */
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody CreateReservationRequest request) {
        log.info("POST /api/v1/reservations - Creating new reservation");
        ReservationResponse response = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get reservation by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id) {
        log.info("GET /api/v1/reservations/{} - Getting reservation by ID", id);
        ReservationResponse response = reservationService.getReservationById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get reservation by reservation number.
     */
    @GetMapping("/number/{reservationNumber}")
    public ResponseEntity<ReservationResponse> getReservationByNumber(
            @PathVariable String reservationNumber) {
        log.info("GET /api/v1/reservations/number/{} - Getting reservation by number", reservationNumber);
        ReservationResponse response = reservationService.getReservationByNumber(reservationNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all reservations with pagination.
     */
    @GetMapping
    public ResponseEntity<Page<ReservationResponse>> getAllReservations(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("GET /api/v1/reservations - Getting all reservations");
        Page<ReservationResponse> response = reservationService.getAllReservations(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get reservations by user ID.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReservationResponse>> getReservationsByUserId(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("GET /api/v1/reservations/user/{} - Getting reservations by user ID", userId);
        Page<ReservationResponse> response = reservationService.getReservationsByUserId(userId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get reservations by riad ID.
     */
    @GetMapping("/riad/{riadId}")
    public ResponseEntity<Page<ReservationResponse>> getReservationsByRiadId(
            @PathVariable Long riadId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("GET /api/v1/reservations/riad/{} - Getting reservations by riad ID", riadId);
        Page<ReservationResponse> response = reservationService.getReservationsByRiadId(riadId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Search reservations with criteria.
     */
    @PostMapping("/search")
    public ResponseEntity<Page<ReservationResponse>> searchReservations(
            @RequestBody ReservationSearchCriteria criteria,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("POST /api/v1/reservations/search - Searching reservations");
        Page<ReservationResponse> response = reservationService.searchReservations(criteria, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Update a reservation.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReservationRequest request) {
        log.info("PUT /api/v1/reservations/{} - Updating reservation", id);
        ReservationResponse response = reservationService.updateReservation(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Confirm a reservation.
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<ReservationResponse> confirmReservation(@PathVariable Long id) {
        log.info("POST /api/v1/reservations/{}/confirm - Confirming reservation", id);
        ReservationResponse response = reservationService.confirmReservation(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel a reservation.
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancelReservation(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        log.info("POST /api/v1/reservations/{}/cancel - Cancelling reservation", id);
        ReservationResponse response = reservationService.cancelReservation(id, reason);
        return ResponseEntity.ok(response);
    }

    /**
     * Check-in a guest.
     */
    @PostMapping("/{id}/check-in")
    public ResponseEntity<ReservationResponse> checkIn(@PathVariable Long id) {
        log.info("POST /api/v1/reservations/{}/check-in - Checking in guest", id);
        ReservationResponse response = reservationService.checkIn(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Check-out a guest.
     */
    @PostMapping("/{id}/check-out")
    public ResponseEntity<ReservationResponse> checkOut(@PathVariable Long id) {
        log.info("POST /api/v1/reservations/{}/check-out - Checking out guest", id);
        ReservationResponse response = reservationService.checkOut(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Mark reservation as no-show.
     */
    @PostMapping("/{id}/no-show")
    public ResponseEntity<ReservationResponse> markNoShow(@PathVariable Long id) {
        log.info("POST /api/v1/reservations/{}/no-show - Marking as no-show", id);
        ReservationResponse response = reservationService.markNoShow(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a reservation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        log.info("DELETE /api/v1/reservations/{} - Deleting reservation", id);
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Check riad availability.
     */
    @PostMapping("/check-availability")
    public ResponseEntity<AvailabilityCheckResponse> checkAvailability(
            @RequestBody AvailabilityCheckRequest request) {
        log.info("POST /api/v1/reservations/check-availability - Checking availability");
        AvailabilityCheckResponse response = reservationService.checkAvailability(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get today's check-ins.
     */
    @GetMapping("/today/check-ins")
    public ResponseEntity<List<ReservationResponse>> getTodayCheckIns() {
        log.info("GET /api/v1/reservations/today/check-ins - Getting today's check-ins");
        List<ReservationResponse> response = reservationService.getTodayCheckIns();
        return ResponseEntity.ok(response);
    }

    /**
     * Get today's check-outs.
     */
    @GetMapping("/today/check-outs")
    public ResponseEntity<List<ReservationResponse>> getTodayCheckOuts() {
        log.info("GET /api/v1/reservations/today/check-outs - Getting today's check-outs");
        List<ReservationResponse> response = reservationService.getTodayCheckOuts();
        return ResponseEntity.ok(response);
    }

    /**
     * Update payment information.
     */
    @PatchMapping("/{id}/payment")
    public ResponseEntity<ReservationResponse> updatePaymentInfo(
            @PathVariable Long id,
            @RequestParam String paymentId) {
        log.info("PATCH /api/v1/reservations/{}/payment - Updating payment info", id);
        ReservationResponse response = reservationService.updatePaymentInfo(id, paymentId);
        return ResponseEntity.ok(response);
    }
}

