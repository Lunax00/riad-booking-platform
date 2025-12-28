package com.code.reservationservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating a new reservation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReservationRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Riad ID is required")
    private Long riadId;

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "At least 1 guest is required")
    @Max(value = 20, message = "Maximum 20 guests allowed")
    private Integer numberOfGuests;

    @NotNull(message = "Number of rooms is required")
    @Min(value = 1, message = "At least 1 room is required")
    @Max(value = 10, message = "Maximum 10 rooms allowed")
    private Integer numberOfRooms;

    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.01", message = "Total price must be greater than 0")
    private BigDecimal totalPrice;

    @DecimalMin(value = "0.00", message = "Deposit amount cannot be negative")
    private BigDecimal depositAmount;

    @Size(max = 3, message = "Currency code must be 3 characters")
    private String currency = "MAD";

    @Size(max = 500, message = "Special requests cannot exceed 500 characters")
    private String specialRequests;

    @NotBlank(message = "Guest name is required")
    @Size(max = 100, message = "Guest name cannot exceed 100 characters")
    private String guestName;

    @NotBlank(message = "Guest email is required")
    @Email(message = "Invalid email format")
    private String guestEmail;

    @Size(max = 20, message = "Guest phone cannot exceed 20 characters")
    private String guestPhone;
}

