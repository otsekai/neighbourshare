package com.example.springproject.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record BookingRequest(
        @NotNull(message = "Item ID is required")
        Long itemId,

        @NotNull(message = "End date is required")
        @Future(message = "End date must be in the future")
        LocalDateTime endDate
) {
}
