package com.code.reservationservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for updating an existing reservation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReservationRequest {

    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkInDate;

    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @Min(value = 1, message = "At least 1 guest is required")
    @Max(value = 20, message = "Maximum 20 guests allowed")
    private Integer numberOfGuests;

    @Min(value = 1, message = "At least 1 room is required")
    @Max(value = 10, message = "Maximum 10 rooms allowed")
    private Integer numberOfRooms;

    @DecimalMin(value = "0.01", message = "Total price must be greater than 0")
    private BigDecimal totalPrice;

    @Size(max = 500, message = "Special requests cannot exceed 500 characters")
    private String specialRequests;

    @Size(max = 100, message = "Guest name cannot exceed 100 characters")
    private String guestName;

    @Email(message = "Invalid email format")
    private String guestEmail;

    @Size(max = 20, message = "Guest phone cannot exceed 20 characters")
    private String guestPhone;
}

