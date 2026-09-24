package com.example.springproject.dto;

import com.example.springproject.entity.BookingStatus;

import java.time.LocalDateTime;

public record BookingResponse(
        Long bookingId,
        Long itemId,
        String itemTitle,
        Long borrowerId,
        String borrowerName,
        Long ownerId,
        String ownerName,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BookingStatus status
) {
}
