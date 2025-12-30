package com.code.reservationservice.dto;

import com.code.reservationservice.dao.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for filtering reservations with various criteria.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationSearchCriteria {

    private Long userId;
    private Long riadId;
    private ReservationStatus status;
    private LocalDate checkInDateFrom;
    private LocalDate checkInDateTo;
    private LocalDate checkOutDateFrom;
    private LocalDate checkOutDateTo;
    private String guestName;
    private String reservationNumber;
}

