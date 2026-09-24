package com.example.springproject.controller;

import com.example.springproject.dto.BookingRequest;
import com.example.springproject.dto.BookingResponse;
import com.example.springproject.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * POST /api/bookings — создание запроса на заимствование (вещь уходит на 10-минутную проверку).
 * GET /api/bookings/inbound — страница «Депо» (список входящих запросов от других людей, которые user_1 должен подтвердить или отклонить).
 * GET /api/bookings/outbound — страница «Депо» (вещи, которые user_1 сам взял у кого-то).
 * PATCH /api/bookings/{id}/approve — владелец согласился отдать вещь.
 * PATCH /api/bookings/{id}/reject — владелец отказал (или сработал 10-минутный таймер).
 */

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping("")
    public ResponseEntity<String> create(@Valid @RequestBody BookingRequest request) {
        bookingService.createBooking(request);
        return ResponseEntity.ok("Booking request created");
    }

    @GetMapping("/inbound")
    public ResponseEntity<List<BookingResponse>> getInbound() {
        return ResponseEntity.ok(bookingService.getIncomingBookings());
    }

    @GetMapping("/outbound")
    public ResponseEntity<List<BookingResponse>> getOutbound() {
        return ResponseEntity.ok(bookingService.getOutgoingBookings());
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<String> approve(@PathVariable long id) {
        bookingService.approveBooking(id);
        return ResponseEntity.ok("Booking approved");
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<String> reject(@PathVariable long id) {
        bookingService.rejectBooking(id);
        return ResponseEntity.ok("Booking rejected");
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<String> complete(@PathVariable long id) {
        bookingService.completeBooking(id);
        return ResponseEntity.ok("Booking completed and item returned");
    }
}
