package com.code.reservationservice.service.impl;

import com.code.reservationservice.dao.entity.Reservation;
import com.code.reservationservice.dao.entity.ReservationStatus;
import com.code.reservationservice.dao.repository.ReservationRepository;
import com.code.reservationservice.dto.*;
import com.code.reservationservice.exception.InvalidReservationOperationException;
import com.code.reservationservice.exception.ReservationNotFoundException;
import com.code.reservationservice.exception.RiadNotAvailableException;
import com.code.reservationservice.mapper.ReservationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReservationServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Reservation reservation;
    private ReservationResponse reservationResponse;
    private CreateReservationRequest createRequest;

    @BeforeEach
    void setUp() {
        reservation = Reservation.builder()
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
        @DisplayName("Should create reservation successfully")
        void shouldCreateReservationSuccessfully() {
            when(reservationRepository.isRiadAvailable(any(), any(), any())).thenReturn(true);
            when(reservationMapper.toEntity(createRequest)).thenReturn(reservation);
            when(reservationRepository.save(reservation)).thenReturn(reservation);
            when(reservationMapper.toResponse(reservation)).thenReturn(reservationResponse);

            ReservationResponse result = reservationService.createReservation(createRequest);

            assertThat(result).isNotNull();
            assertThat(result.getReservationNumber()).isEqualTo("RES-12345678");
            verify(reservationRepository).save(reservation);
        }

        @Test
        @DisplayName("Should throw RiadNotAvailableException when riad is not available")
        void shouldThrowRiadNotAvailableExceptionWhenRiadNotAvailable() {
            when(reservationRepository.isRiadAvailable(any(), any(), any())).thenReturn(false);

            assertThatThrownBy(() -> reservationService.createReservation(createRequest))
                    .isInstanceOf(RiadNotAvailableException.class);

            verify(reservationRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when check-in date is in the past")
        void shouldThrowIllegalArgumentExceptionWhenCheckInDateInPast() {
            CreateReservationRequest invalidRequest = CreateReservationRequest.builder()
                    .userId(1L)
                    .riadId(100L)
                    .checkInDate(LocalDate.now().minusDays(1))
                    .checkOutDate(LocalDate.now().plusDays(3))
                    .numberOfGuests(2)
                    .numberOfRooms(1)
                    .totalPrice(new BigDecimal("500.00"))
                    .guestName("John Doe")
                    .guestEmail("john@example.com")
                    .build();

            assertThatThrownBy(() -> reservationService.createReservation(invalidRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Check-in date cannot be in the past");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when check-out date is before check-in")
        void shouldThrowIllegalArgumentExceptionWhenCheckOutBeforeCheckIn() {
            CreateReservationRequest invalidRequest = CreateReservationRequest.builder()
                    .userId(1L)
                    .riadId(100L)
                    .checkInDate(LocalDate.now().plusDays(5))
                    .checkOutDate(LocalDate.now().plusDays(3))
                    .numberOfGuests(2)
                    .numberOfRooms(1)
                    .totalPrice(new BigDecimal("500.00"))
                    .guestName("John Doe")
                    .guestEmail("john@example.com")
                    .build();

            assertThatThrownBy(() -> reservationService.createReservation(invalidRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Check-out date must be after check-in date");
        }
    }

    @Nested
    @DisplayName("Get Reservation By ID")
    class GetReservationById {

        @Test
        @DisplayName("Should return reservation when found")
        void shouldReturnReservationWhenFound() {
            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
            when(reservationMapper.toResponse(reservation)).thenReturn(reservationResponse);

            ReservationResponse result = reservationService.getReservationById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw ReservationNotFoundException when not found")
        void shouldThrowReservationNotFoundExceptionWhenNotFound() {
            when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservationService.getReservationById(999L))
                    .isInstanceOf(ReservationNotFoundException.class)
                    .hasMessage("Reservation not found with id: 999");
        }
    }

    @Nested
    @DisplayName("Get Reservation By Number")
    class GetReservationByNumber {

        @Test
        @DisplayName("Should return reservation when found")
        void shouldReturnReservationWhenFound() {
            when(reservationRepository.findByReservationNumber("RES-12345678"))
                    .thenReturn(Optional.of(reservation));
            when(reservationMapper.toResponse(reservation)).thenReturn(reservationResponse);

            ReservationResponse result = reservationService.getReservationByNumber("RES-12345678");

            assertThat(result).isNotNull();
            assertThat(result.getReservationNumber()).isEqualTo("RES-12345678");
        }

        @Test
        @DisplayName("Should throw ReservationNotFoundException when not found")
        void shouldThrowReservationNotFoundExceptionWhenNotFound() {
            when(reservationRepository.findByReservationNumber("RES-NOTEXIST"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservationService.getReservationByNumber("RES-NOTEXIST"))
                    .isInstanceOf(ReservationNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Get All Reservations")
    class GetAllReservations {

        @Test
        @DisplayName("Should return page of reservations")
        void shouldReturnPageOfReservations() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Reservation> reservationPage = new PageImpl<>(List.of(reservation));

            when(reservationRepository.findAll(pageable)).thenReturn(reservationPage);
            when(reservationMapper.toResponse(reservation)).thenReturn(reservationResponse);

            Page<ReservationResponse> result = reservationService.getAllReservations(pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("Get Reservations By User ID")
    class GetReservationsByUserId {

        @Test
        @DisplayName("Should return page of user reservations")
        void shouldReturnPageOfUserReservations() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Reservation> reservationPage = new PageImpl<>(List.of(reservation));

            when(reservationRepository.findByUserId(1L, pageable)).thenReturn(reservationPage);
            when(reservationMapper.toResponse(reservation)).thenReturn(reservationResponse);

            Page<ReservationResponse> result = reservationService.getReservationsByUserId(1L, pageable);

            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Get Reservations By Riad ID")
    class GetReservationsByRiadId {

        @Test
        @DisplayName("Should return page of riad reservations")
        void shouldReturnPageOfRiadReservations() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Reservation> reservationPage = new PageImpl<>(List.of(reservation));

            when(reservationRepository.findByRiadId(100L, pageable)).thenReturn(reservationPage);
            when(reservationMapper.toResponse(reservation)).thenReturn(reservationResponse);

            Page<ReservationResponse> result = reservationService.getReservationsByRiadId(100L, pageable);

            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Search Reservations")
    class SearchReservations {

        @Test
        @DisplayName("Should search reservations with criteria")
        @SuppressWarnings("unchecked")
        void shouldSearchReservationsWithCriteria() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Reservation> reservationPage = new PageImpl<>(List.of(reservation));
            ReservationSearchCriteria criteria = ReservationSearchCriteria.builder()
                    .userId(1L)
                    .status(ReservationStatus.PENDING)
                    .build();

            when(reservationRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(reservationPage);
            when(reservationMapper.toResponse(reservation)).thenReturn(reservationResponse);

            Page<ReservationResponse> result = reservationService.searchReservations(criteria, pageable);

            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Confirm Reservation")
    class ConfirmReservation {

        @Test
        @DisplayName("Should confirm pending reservation")
        void shouldConfirmPendingReservation() {
            reservation.setStatus(ReservationStatus.PENDING);
            Reservation confirmedReservation = Reservation.builder()
                    .id(1L)
                    .status(ReservationStatus.CONFIRMED)
                    .build();
            ReservationResponse confirmedResponse = ReservationResponse.builder()
                    .id(1L)
                    .status(ReservationStatus.CONFIRMED)
                    .build();

            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
            when(reservationRepository.save(any())).thenReturn(confirmedReservation);
            when(reservationMapper.toResponse(any())).thenReturn(confirmedResponse);

            ReservationResponse result = reservationService.confirmReservation(1L);

            assertThat(result.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        }

        @Test
        @DisplayName("Should throw exception when confirming non-pending reservation")
        void shouldThrowExceptionWhenConfirmingNonPendingReservation() {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

            assertThatThrownBy(() -> reservationService.confirmReservation(1L))
                    .isInstanceOf(InvalidReservationOperationException.class)
                    .hasMessageContaining("Can only confirm PENDING reservations");
        }
    }

    @Nested
    @DisplayName("Cancel Reservation")
    class CancelReservation {

        @Test
        @DisplayName("Should cancel reservation with reason")
        void shouldCancelReservationWithReason() {
            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
            when(reservationRepository.save(any())).thenReturn(reservation);
            when(reservationMapper.toResponse(any())).thenReturn(reservationResponse);

            reservationService.cancelReservation(1L, "Guest requested cancellation");

            ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
            verify(reservationRepository).save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(ReservationStatus.CANCELLED);
            assertThat(captor.getValue().getCancellationReason()).isEqualTo("Guest requested cancellation");
        }

        @Test
        @DisplayName("Should throw exception when cancelling already cancelled reservation")
        void shouldThrowExceptionWhenCancellingAlreadyCancelledReservation() {
            reservation.setStatus(ReservationStatus.CANCELLED);
            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

            assertThatThrownBy(() -> reservationService.cancelReservation(1L, "reason"))
                    .isInstanceOf(InvalidReservationOperationException.class)
                    .hasMessageContaining("Cannot cancel reservation");
        }
    }

    @Nested
    @DisplayName("Check In")
    class CheckIn {

        @Test
        @DisplayName("Should check in confirmed reservation")
        void shouldCheckInConfirmedReservation() {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
            when(reservationRepository.save(any())).thenReturn(reservation);
            when(reservationMapper.toResponse(any())).thenReturn(reservationResponse);

            reservationService.checkIn(1L);

            ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
            verify(reservationRepository).save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(ReservationStatus.CHECKED_IN);
        }

        @Test
        @DisplayName("Should throw exception when checking in non-confirmed reservation")
        void shouldThrowExceptionWhenCheckingInNonConfirmedReservation() {
            reservation.setStatus(ReservationStatus.PENDING);
            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

            assertThatThrownBy(() -> reservationService.checkIn(1L))
                    .isInstanceOf(InvalidReservationOperationException.class)
                    .hasMessageContaining("Can only check-in CONFIRMED reservations");
        }
    }

    @Nested
    @DisplayName("Check Out")
    class CheckOut {

        @Test
        @DisplayName("Should check out checked-in reservation")
        void shouldCheckOutCheckedInReservation() {
            reservation.setStatus(ReservationStatus.CHECKED_IN);
            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
            when(reservationRepository.save(any())).thenReturn(reservation);
            when(reservationMapper.toResponse(any())).thenReturn(reservationResponse);

            reservationService.checkOut(1L);

            ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
            verify(reservationRepository).save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(ReservationStatus.CHECKED_OUT);
        }

        @Test
        @DisplayName("Should throw exception when checking out non-checked-in reservation")
        void shouldThrowExceptionWhenCheckingOutNonCheckedInReservation() {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

            assertThatThrownBy(() -> reservationService.checkOut(1L))
                    .isInstanceOf(InvalidReservationOperationException.class)
                    .hasMessageContaining("Can only check-out CHECKED_IN reservations");
        }
    }

    @Nested
    @DisplayName("Mark No Show")
    class MarkNoShow {

        @Test
        @DisplayName("Should mark confirmed reservation as no-show")
        void shouldMarkConfirmedReservationAsNoShow() {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
            when(reservationRepository.save(any())).thenReturn(reservation);
            when(reservationMapper.toResponse(any())).thenReturn(reservationResponse);

            reservationService.markNoShow(1L);

            ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
            verify(reservationRepository).save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(ReservationStatus.NO_SHOW);
        }

        @Test
        @DisplayName("Should throw exception when marking non-confirmed reservation as no-show")
        void shouldThrowExceptionWhenMarkingNonConfirmedAsNoShow() {
            reservation.setStatus(ReservationStatus.PENDING);
            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

            assertThatThrownBy(() -> reservationService.markNoShow(1L))
                    .isInstanceOf(InvalidReservationOperationException.class)
                    .hasMessageContaining("Can only mark CONFIRMED reservations as no-show");
        }
    }

    @Nested
    @DisplayName("Delete Reservation")
    class DeleteReservation {

        @Test
        @DisplayName("Should delete reservation")
        void shouldDeleteReservation() {
            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

            reservationService.deleteReservation(1L);

            verify(reservationRepository).delete(reservation);
        }

        @Test
        @DisplayName("Should throw exception when reservation not found")
        void shouldThrowExceptionWhenReservationNotFound() {
            when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservationService.deleteReservation(999L))
                    .isInstanceOf(ReservationNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Check Availability")
    class CheckAvailability {

        @Test
        @DisplayName("Should return available when riad is available")
        void shouldReturnAvailableWhenRiadIsAvailable() {
            AvailabilityCheckRequest request = AvailabilityCheckRequest.builder()
                    .riadId(100L)
                    .checkInDate(LocalDate.now().plusDays(1))
                    .checkOutDate(LocalDate.now().plusDays(3))
                    .build();

            when(reservationRepository.isRiadAvailable(100L,
                    LocalDate.now().plusDays(1), LocalDate.now().plusDays(3)))
                    .thenReturn(true);

            AvailabilityCheckResponse result = reservationService.checkAvailability(request);

            assertThat(result.isAvailable()).isTrue();
            assertThat(result.getMessage()).isEqualTo("Riad is available for the selected dates");
        }

        @Test
        @DisplayName("Should return not available when riad is booked")
        void shouldReturnNotAvailableWhenRiadIsBooked() {
            AvailabilityCheckRequest request = AvailabilityCheckRequest.builder()
                    .riadId(100L)
                    .checkInDate(LocalDate.now().plusDays(1))
                    .checkOutDate(LocalDate.now().plusDays(3))
                    .build();

            when(reservationRepository.isRiadAvailable(100L,
                    LocalDate.now().plusDays(1), LocalDate.now().plusDays(3)))
                    .thenReturn(false);

            AvailabilityCheckResponse result = reservationService.checkAvailability(request);

            assertThat(result.isAvailable()).isFalse();
            assertThat(result.getMessage()).isEqualTo("Riad is not available for the selected dates");
        }
    }

    @Nested
    @DisplayName("Get Today Check-ins and Check-outs")
    class GetTodayCheckInsAndCheckOuts {

        @Test
        @DisplayName("Should return today's check-ins")
        void shouldReturnTodayCheckIns() {
            when(reservationRepository.findByCheckInDateAndStatus(
                    LocalDate.now(), ReservationStatus.CONFIRMED))
                    .thenReturn(List.of(reservation));
            when(reservationMapper.toResponse(reservation)).thenReturn(reservationResponse);

            List<ReservationResponse> result = reservationService.getTodayCheckIns();

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should return today's check-outs")
        void shouldReturnTodayCheckOuts() {
            reservation.setStatus(ReservationStatus.CHECKED_IN);
            when(reservationRepository.findByCheckOutDateAndStatus(
                    LocalDate.now(), ReservationStatus.CHECKED_IN))
                    .thenReturn(List.of(reservation));
            when(reservationMapper.toResponse(reservation)).thenReturn(reservationResponse);

            List<ReservationResponse> result = reservationService.getTodayCheckOuts();

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Update Payment Info")
    class UpdatePaymentInfo {

        @Test
        @DisplayName("Should update payment info")
        void shouldUpdatePaymentInfo() {
            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
            when(reservationRepository.save(any())).thenReturn(reservation);
            when(reservationMapper.toResponse(any())).thenReturn(reservationResponse);

            reservationService.updatePaymentInfo(1L, "PAY-123456");

            ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
            verify(reservationRepository).save(captor.capture());
            assertThat(captor.getValue().getPaymentId()).isEqualTo("PAY-123456");
        }
    }

    @Nested
    @DisplayName("Expire Pending Reservations")
    class ExpirePendingReservations {

        @Test
        @DisplayName("Should expire pending reservations older than specified hours")
        void shouldExpirePendingReservations() {
            Reservation pendingReservation = Reservation.builder()
                    .id(1L)
                    .status(ReservationStatus.PENDING)
                    .build();

            when(reservationRepository.findPendingReservationsOlderThan(any(LocalDateTime.class)))
                    .thenReturn(List.of(pendingReservation));

            int expiredCount = reservationService.expirePendingReservations(24);

            assertThat(expiredCount).isEqualTo(1);
            verify(reservationRepository).save(pendingReservation);
            assertThat(pendingReservation.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        }

        @Test
        @DisplayName("Should return 0 when no pending reservations to expire")
        void shouldReturnZeroWhenNoPendingReservationsToExpire() {
            when(reservationRepository.findPendingReservationsOlderThan(any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            int expiredCount = reservationService.expirePendingReservations(24);

            assertThat(expiredCount).isEqualTo(0);
            verify(reservationRepository, never()).save(any());
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
                    .guestName("Updated Name")
                    .build();

            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
            when(reservationRepository.save(any())).thenReturn(reservation);
            when(reservationMapper.toResponse(any())).thenReturn(reservationResponse);

            ReservationResponse result = reservationService.updateReservation(1L, updateRequest);

            assertThat(result).isNotNull();
            verify(reservationMapper).updateEntityFromRequest(updateRequest, reservation);
            verify(reservationRepository).save(reservation);
        }

        @Test
        @DisplayName("Should throw exception when updating non-pending/confirmed reservation")
        void shouldThrowExceptionWhenUpdatingNonPendingConfirmedReservation() {
            reservation.setStatus(ReservationStatus.CANCELLED);
            UpdateReservationRequest updateRequest = UpdateReservationRequest.builder()
                    .numberOfGuests(4)
                    .build();

            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

            assertThatThrownBy(() -> reservationService.updateReservation(1L, updateRequest))
                    .isInstanceOf(InvalidReservationOperationException.class)
                    .hasMessageContaining("Cannot update reservation with status: CANCELLED");
        }
    }
}

