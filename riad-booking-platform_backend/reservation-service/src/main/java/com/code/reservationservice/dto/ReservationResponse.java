package com.code.reservationservice.dto;

import com.code.reservationservice.dao.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for reservation response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {

    private Long id;
    private String reservationNumber;
    private Long userId;
    private Long riadId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private Integer numberOfRooms;
    private ReservationStatus status;
    private BigDecimal totalPrice;
    private BigDecimal depositAmount;
    private String currency;
    private String specialRequests;
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private String paymentId;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

