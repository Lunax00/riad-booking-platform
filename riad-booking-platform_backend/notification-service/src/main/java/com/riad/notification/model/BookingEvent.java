package com.riad.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent implements Serializable {

    private String bookingId;
    private String customerEmail;
    private String customerPhone;
    private String customerName;
    private String riadName;
    private String checkInDate;
    private String checkOutDate;
    private Double totalAmount;
    private String paymentStatus;
    private EventType eventType;

    public enum EventType {
        BOOKING_CONFIRMED,
        BOOKING_CANCELLED,
        PAYMENT_RECEIVED,
        REMINDER_CHECKIN,
        REMINDER_CHECKOUT
    }
}

