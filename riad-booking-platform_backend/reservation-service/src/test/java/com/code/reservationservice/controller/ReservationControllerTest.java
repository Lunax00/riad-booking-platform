package com.code.reservationservice.controller;

import com.code.reservationservice.dao.entity.ReservationStatus;
import com.code.reservationservice.dto.*;
import com.code.reservationservice.exception.InvalidReservationOperationException;
import com.code.reservationservice.exception.ReservationNotFoundException;
import com.code.reservationservice.exception.RiadNotAvailableException;
import com.code.reservationservice.service.ReservationService;
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
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReservationController.
 */
@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private ReservationResponse reservationResponse;
    private CreateReservationRequest createRequest;

    @BeforeEach
    void setUp() {
        reservationResponse = ReservationResponse.builder()
                .id(1L)
                .reservationNumber("RES-12345678")
                .userId(1L)
                .riadId(100L)
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(3))
                .numberOfGuests(2)
                .numberOfRooms(1)
                .status(ReservationStatus.PENDING)
                .totalPrice(new BigDecimal("500.00"))
                .guestName("John Doe")
                .guestEmail("john@example.com")
                .build();

        createRequest = CreateReservationRequest.builder()
                .userId(1L)
                .riadId(100L)
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(3))
                .numberOfGuests(2)
                .numberOfRooms(1)
                .totalPrice(new BigDecimal("500.00"))
                .guestName("John Doe")
                .guestEmail("john@example.com")
                .build();
    }

    @Nested
    @DisplayName("Create Reservation")
    class CreateReservation {

        @Test
        @DisplayName("Should create reservation and return 201")
        void shouldCreateReservationAndReturn201() {
            when(reservationService.createReservation(any(CreateReservationRequest.class)))
                    .thenReturn(reservationResponse);

            ResponseEntity<ReservationResponse> response =
                reservationController.createReservation(createRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getId()).isEqualTo(1L);
            assertThat(response.getBody().getReservationNumber()).isEqualTo("RES-12345678");
        }

        @Test
        @DisplayName("Should throw exception when riad not available")
        void shouldThrowExceptionWhenRiadNotAvailable() {
            when(reservationService.createReservation(any(CreateReservationRequest.class)))
                    .thenThrow(new RiadNotAvailableException(100L));

            assertThatThrownBy(() -> reservationController.createReservation(createRequest))
                    .isInstanceOf(RiadNotAvailableException.class);
        }
    }

    @Nested
    @DisplayName("Get Reservation By ID")
    class GetReservationById {

        @Test
        @DisplayName("Should return reservation when found")
        void shouldReturnReservationWhenFound() {
            when(reservationService.getReservationById(1L)).thenReturn(reservationResponse);

            ResponseEntity<ReservationResponse> response = reservationController.getReservationById(1L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw exception when reservation not found")
        void shouldThrowExceptionWhenReservationNotFound() {
            when(reservationService.getReservationById(999L))
                    .thenThrow(new ReservationNotFoundException(999L));

            assertThatThrownBy(() -> reservationController.getReservationById(999L))
                    .isInstanceOf(ReservationNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Get Reservation By Number")
    class GetReservationByNumber {

        @Test
        @DisplayName("Should return reservation when found by number")
        void shouldReturnReservationWhenFoundByNumber() {
            when(reservationService.getReservationByNumber("RES-12345678"))
                    .thenReturn(reservationResponse);

            ResponseEntity<ReservationResponse> response =
                reservationController.getReservationByNumber("RES-12345678");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getReservationNumber()).isEqualTo("RES-12345678");
        }
    }

    @Nested
    @DisplayName("Get All Reservations")
    class GetAllReservations {

        @Test
        @DisplayName("Should return page of reservations")
        void shouldReturnPageOfReservations() {
            Page<ReservationResponse> page = new PageImpl<>(List.of(reservationResponse));
            Pageable pageable = PageRequest.of(0, 20);
            when(reservationService.getAllReservations(any(Pageable.class))).thenReturn(page);

            ResponseEntity<Page<ReservationResponse>> response =
                reservationController.getAllReservations(pageable);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Get Reservations By User ID")
    class GetReservationsByUserId {

        @Test
        @DisplayName("Should return user reservations")
        void shouldReturnUserReservations() {
            Page<ReservationResponse> page = new PageImpl<>(List.of(reservationResponse));
            Pageable pageable = PageRequest.of(0, 20);
            when(reservationService.getReservationsByUserId(eq(1L), any(Pageable.class)))
                    .thenReturn(page);

            ResponseEntity<Page<ReservationResponse>> response =
                reservationController.getReservationsByUserId(1L, pageable);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("Get Reservations By Riad ID")
    class GetReservationsByRiadId {

        @Test
        @DisplayName("Should return riad reservations")
        void shouldReturnRiadReservations() {
            Page<ReservationResponse> page = new PageImpl<>(List.of(reservationResponse));
            Pageable pageable = PageRequest.of(0, 20);
            when(reservationService.getReservationsByRiadId(eq(100L), any(Pageable.class)))
                    .thenReturn(page);

            ResponseEntity<Page<ReservationResponse>> response =
                reservationController.getReservationsByRiadId(100L, pageable);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("Search Reservations")
    class SearchReservations {

        @Test
        @DisplayName("Should search reservations with criteria")
        void shouldSearchReservationsWithCriteria() {
            Page<ReservationResponse> page = new PageImpl<>(List.of(reservationResponse));
            Pageable pageable = PageRequest.of(0, 20);
            ReservationSearchCriteria criteria = ReservationSearchCriteria.builder()
                    .userId(1L)
                    .build();

            when(reservationService.searchReservations(any(ReservationSearchCriteria.class), any(Pageable.class)))
                    .thenReturn(page);

            ResponseEntity<Page<ReservationResponse>> response =
                reservationController.searchReservations(criteria, pageable);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("Update Reservation")
    class UpdateReservation {

        @Test
        @DisplayName("Should update reservation successfully")
        void shouldUpdateReservationSuccessfully() {
            UpdateReservationRequest updateRequest = UpdateReservationRequest.builder()
                    .numberOfGuests(4)
                    .build();

            when(reservationService.updateReservation(eq(1L), any(UpdateReservationRequest.class)))
                    .thenReturn(reservationResponse);

            ResponseEntity<ReservationResponse> response =
                reservationController.updateReservation(1L, updateRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("Confirm Reservation")
    class ConfirmReservation {

        @Test
        @DisplayName("Should confirm reservation successfully")
        void shouldConfirmReservationSuccessfully() {
            reservationResponse.setStatus(ReservationStatus.CONFIRMED);
            when(reservationService.confirmReservation(1L)).thenReturn(reservationResponse);

            ResponseEntity<ReservationResponse> response = reservationController.confirmReservation(1L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        }

        @Test
        @DisplayName("Should throw exception when confirming non-pending reservation")
        void shouldThrowExceptionWhenConfirmingNonPendingReservation() {
            when(reservationService.confirmReservation(1L))
                    .thenThrow(new InvalidReservationOperationException("Cannot confirm"));

            assertThatThrownBy(() -> reservationController.confirmReservation(1L))
                    .isInstanceOf(InvalidReservationOperationException.class);
        }
    }

    @Nested
    @DisplayName("Cancel Reservation")
    class CancelReservation {

        @Test
        @DisplayName("Should cancel reservation successfully")
        void shouldCancelReservationSuccessfully() {
            reservationResponse.setStatus(ReservationStatus.CANCELLED);
            when(reservationService.cancelReservation(eq(1L), any())).thenReturn(reservationResponse);

            ResponseEntity<ReservationResponse> response =
                reservationController.cancelReservation(1L, "Guest requested");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        }
    }

    @Nested
    @DisplayName("Check In")
    class CheckIn {

        @Test
        @DisplayName("Should check in successfully")
        void shouldCheckInSuccessfully() {
            reservationResponse.setStatus(ReservationStatus.CHECKED_IN);
            when(reservationService.checkIn(1L)).thenReturn(reservationResponse);

            ResponseEntity<ReservationResponse> response = reservationController.checkIn(1L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getStatus()).isEqualTo(ReservationStatus.CHECKED_IN);
        }
    }

    @Nested
    @DisplayName("Check Out")
    class CheckOut {

        @Test
        @DisplayName("Should check out successfully")
        void shouldCheckOutSuccessfully() {
            reservationResponse.setStatus(ReservationStatus.CHECKED_OUT);
            when(reservationService.checkOut(1L)).thenReturn(reservationResponse);

            ResponseEntity<ReservationResponse> response = reservationController.checkOut(1L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getStatus()).isEqualTo(ReservationStatus.CHECKED_OUT);
        }
    }

    @Nested
    @DisplayName("Mark No Show")
    class MarkNoShow {

        @Test
        @DisplayName("Should mark as no-show successfully")
        void shouldMarkAsNoShowSuccessfully() {
            reservationResponse.setStatus(ReservationStatus.NO_SHOW);
            when(reservationService.markNoShow(1L)).thenReturn(reservationResponse);

            ResponseEntity<ReservationResponse> response = reservationController.markNoShow(1L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getStatus()).isEqualTo(ReservationStatus.NO_SHOW);
        }
    }

    @Nested
    @DisplayName("Delete Reservation")
    class DeleteReservation {

        @Test
        @DisplayName("Should delete reservation and return 204")
        void shouldDeleteReservationAndReturn204() {
            doNothing().when(reservationService).deleteReservation(1L);

            ResponseEntity<Void> response = reservationController.deleteReservation(1L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(reservationService).deleteReservation(1L);
        }

        @Test
        @DisplayName("Should throw exception when reservation not found")
        void shouldThrowExceptionWhenReservationNotFound() {
            doThrow(new ReservationNotFoundException(999L))
                    .when(reservationService).deleteReservation(999L);

            assertThatThrownBy(() -> reservationController.deleteReservation(999L))
                    .isInstanceOf(ReservationNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Check Availability")
    class CheckAvailability {

        @Test
        @DisplayName("Should check availability and return response")
        void shouldCheckAvailabilityAndReturnResponse() {
            AvailabilityCheckRequest request = AvailabilityCheckRequest.builder()
                    .riadId(100L)
                    .checkInDate(LocalDate.now().plusDays(1))
                    .checkOutDate(LocalDate.now().plusDays(3))
                    .build();

            AvailabilityCheckResponse availabilityResponse = AvailabilityCheckResponse.builder()
                    .riadId(100L)
                    .available(true)
                    .message("Riad is available")
                    .build();

            when(reservationService.checkAvailability(any(AvailabilityCheckRequest.class)))
                    .thenReturn(availabilityResponse);

            ResponseEntity<AvailabilityCheckResponse> response =
                reservationController.checkAvailability(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().isAvailable()).isTrue();
        }
    }

    @Nested
    @DisplayName("Today Check-ins and Check-outs")
    class TodayCheckInsAndCheckOuts {

        @Test
        @DisplayName("Should return today's check-ins")
        void shouldReturnTodayCheckIns() {
            when(reservationService.getTodayCheckIns()).thenReturn(List.of(reservationResponse));

            ResponseEntity<List<ReservationResponse>> response = reservationController.getTodayCheckIns();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(1);
        }

        @Test
        @DisplayName("Should return today's check-outs")
        void shouldReturnTodayCheckOuts() {
            when(reservationService.getTodayCheckOuts()).thenReturn(List.of(reservationResponse));

            ResponseEntity<List<ReservationResponse>> response = reservationController.getTodayCheckOuts();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Update Payment Info")
    class UpdatePaymentInfo {

        @Test
        @DisplayName("Should update payment info successfully")
        void shouldUpdatePaymentInfoSuccessfully() {
            reservationResponse.setPaymentId("PAY-123456");
            when(reservationService.updatePaymentInfo(1L, "PAY-123456"))
                    .thenReturn(reservationResponse);

            ResponseEntity<ReservationResponse> response =
                reservationController.updatePaymentInfo(1L, "PAY-123456");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getPaymentId()).isEqualTo("PAY-123456");
        }
    }
}

