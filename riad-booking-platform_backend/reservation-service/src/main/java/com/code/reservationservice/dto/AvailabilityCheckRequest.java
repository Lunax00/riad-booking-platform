package com.code.reservationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for checking riad availability.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityCheckRequest {

    private Long riadId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}

