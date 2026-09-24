package com.example.springproject.scheduler;

import com.example.springproject.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingScheduler {
    private final BookingService bookingService;

    @Scheduled(fixedRate = 60000)
    public void autoReject() {
        bookingService.autoRejectPendingBookings();
    }
}
