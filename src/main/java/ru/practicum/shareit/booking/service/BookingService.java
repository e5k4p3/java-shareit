package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking addBooking(BookingDto bookingDto, Long userId);

    Booking updateBooking(Long bookingId, Long userId, Boolean status);

    Booking getBookingById(Long bookingId, Long userId);

    List<Booking> getAllBookingsByUserId(Long userId, String state);

    List<Booking> getAllBookingsByOwnerId(Long userId, String state);
}
