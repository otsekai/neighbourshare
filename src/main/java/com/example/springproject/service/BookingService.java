package com.example.springproject.service;

import com.example.springproject.dto.BookingRequest;
import com.example.springproject.dto.BookingResponse;
import com.example.springproject.entity.*;
import com.example.springproject.repository.BookingRepository;
import com.example.springproject.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserContext context;
    private final ItemRepository itemRepository;

    @Transactional
    public void createBooking(BookingRequest request) {
        context.checkNotBanned();
        User borrower = context.getCurrentUser();
        Item item = itemRepository.findById(request.itemId())
                .orElseThrow(() -> new RuntimeException("Item with id: " + request.itemId() + " not found"));

        if (borrower.getUserId().equals(item.getOwner().getUserId())) {
            throw new RuntimeException("You cannot book your own item");
        }

        if (item.getActualStatus() != ItemStatus.available) {
            throw new RuntimeException("Item with id: " + request.itemId() + " is not available");
        }

        boolean hasActive = bookingRepository.existsByItemItemIdAndBookingStatusIn(
                item.getItemId(),
                List.of(BookingStatus.pending, BookingStatus.approved)
        );

        if (hasActive) {
            throw new RuntimeException("Item already has an active booking request");
        }

        if (request.endDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("End date cannot be before current date");
        }

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBorrower(borrower);
        booking.setEndDate(request.endDate());
        booking.setBookingStatus(BookingStatus.pending);

        bookingRepository.save(booking);

        item.setActualStatus(ItemStatus.rented);
        itemRepository.save(item);

        logBooking("created", booking);
    }

    public List<BookingResponse> getIncomingBookings() {
        User currentUser = context.getCurrentUser();
        List<Booking> bookings = bookingRepository.findBookingsByOwnerId(currentUser.getUserId());

        return bookings.stream().map(this::toResponse).toList();
    }

    public List<BookingResponse> getOutgoingBookings() {
        User currentUser = context.getCurrentUser();
        List<Booking> bookings = bookingRepository.findByBorrowerUserId(currentUser.getUserId());

        return bookings.stream().map(this::toResponse).toList();
    }

    @Transactional
    public void approveBooking(Long bookingId) {
        context.checkNotBanned();
        Booking booking = getBookingAndValidateOwner(bookingId);

        if (booking.getBookingStatus() != BookingStatus.pending) {
            throw new RuntimeException("Booking is not in pending state");
        }

        booking.setBookingStatus(BookingStatus.approved);
        bookingRepository.save(booking);

        logBooking("approved", booking);
    }

    @Transactional
    public void rejectBooking(Long bookingId) {
        context.checkNotBanned();
        Booking booking = getBookingAndValidateOwner(bookingId);

        if (booking.getBookingStatus() != BookingStatus.pending) {
            throw new RuntimeException("Booking is not in pending state");
        }

        booking.setBookingStatus(BookingStatus.rejected);
        bookingRepository.save(booking);

        Item item = booking.getItem();
        item.setActualStatus(ItemStatus.available);
        itemRepository.save(item);

        logBooking("rejected", booking);
    }

    @Transactional
    public void autoRejectPendingBookings() {
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        List<Booking> pending = bookingRepository.findByBookingStatusAndCreatedAtBefore(
                BookingStatus.pending, tenMinutesAgo
        );

        for (Booking booking : pending) {
            booking.setBookingStatus(BookingStatus.rejected);
            bookingRepository.save(booking);

            Item item = booking.getItem();
            if (item.getActualStatus() == ItemStatus.rented) {
                item.setActualStatus(ItemStatus.available);
                itemRepository.save(item);
            }

            logBooking("auto rejected", booking);
        }
    }

    @Transactional
    public void completeBooking(Long bookingId) {
        context.checkNotBanned();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking with id: " + bookingId + " not found"));

        User currentUser = context.getCurrentUser();
        if (!booking.getItem().getOwner().getUserId().equals(currentUser.getUserId())
                && !booking.getBorrower().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("You are not related to this booking");
        }
        if (booking.getBookingStatus() != BookingStatus.approved) {
            throw new RuntimeException("Only approved bookings can be completed");
        }
        booking.setBookingStatus(BookingStatus.completed);
        bookingRepository.save(booking);

        Item item = booking.getItem();
        item.setActualStatus(ItemStatus.available);
        itemRepository.save(item);

        logBooking("completed", booking);
    }

    private void logBooking(String message, Booking booking) {
        User currentUser = context.getCurrentUser();

        log.info("Booking {}: id={}, item={}, owner={}, borrower={}", message,
                booking.getBookingId(),
                booking.getItem().getTitle(),
                booking.getItem().getOwner().getEmail(),
                booking.getBorrower().getEmail()
        );
    }

    private Booking getBookingAndValidateOwner(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking with id: " + bookingId + " not found"));

        User currentUser = context.getCurrentUser();
        if (!booking.getItem().getOwner().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("You are not the owner of this item");
        }

        return booking;
    }

    private BookingResponse toResponse(Booking b) {
        return new BookingResponse(
                b.getBookingId(),
                b.getItem().getItemId(),
                b.getItem().getTitle(),
                b.getBorrower().getUserId(),
                b.getBorrower().getName(),
                b.getItem().getOwner().getUserId(),
                b.getItem().getOwner().getName(),
                b.getStartDate(),
                b.getEndDate(),
                b.getBookingStatus()
        );
    }
}
