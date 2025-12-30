package com.code.reservationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for availability check response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityCheckResponse {

    private Long riadId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private boolean available;
    private String message;
}

