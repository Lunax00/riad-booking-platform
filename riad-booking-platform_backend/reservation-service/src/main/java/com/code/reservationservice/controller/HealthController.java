package com.code.reservationservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health and info endpoints for the reservation service.
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "reservation-service");
        health.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(health);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Reservation Service");
        info.put("description", "Microservice for managing riad reservations in Marrakech");
        info.put("version", "1.0.0");
        info.put("endpoints", new String[]{
            "POST /api/v1/reservations - Create reservation",
            "GET /api/v1/reservations/{id} - Get reservation by ID",
            "GET /api/v1/reservations/number/{number} - Get by reservation number",
            "GET /api/v1/reservations - Get all reservations",
            "GET /api/v1/reservations/user/{userId} - Get user reservations",
            "GET /api/v1/reservations/riad/{riadId} - Get riad reservations",
            "POST /api/v1/reservations/search - Search reservations",
            "PUT /api/v1/reservations/{id} - Update reservation",
            "POST /api/v1/reservations/{id}/confirm - Confirm reservation",
            "POST /api/v1/reservations/{id}/cancel - Cancel reservation",
            "POST /api/v1/reservations/{id}/check-in - Check in",
            "POST /api/v1/reservations/{id}/check-out - Check out",
            "DELETE /api/v1/reservations/{id} - Delete reservation",
            "POST /api/v1/reservations/check-availability - Check availability",
            "GET /api/v1/reservations/today/check-ins - Today's check-ins",
            "GET /api/v1/reservations/today/check-outs - Today's check-outs"
        });
        return ResponseEntity.ok(info);
    }
}

