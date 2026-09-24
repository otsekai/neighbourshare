package com.example.springproject.repository;

import com.example.springproject.entity.Booking;
import com.example.springproject.entity.BookingStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.item.owner.userId = :ownerId AND b.bookingStatus = 'pending'")
    List<Booking> findBookingsByOwnerId(@Param("ownerId") Long ownerId);

    List<Booking> findByBorrowerUserId(Long borrowerUserId);

    boolean existsByItemItemIdAndBookingStatusIn(Long itemId, List<BookingStatus> bookingStatuses);

    List<Booking> findByBookingStatusAndCreatedAtBefore(BookingStatus bookingStatus, LocalDateTime time);
}
