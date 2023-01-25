package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.practicum.shareit.booking.model.enums.BookingStatus.WAITING;

public class BookingMapperTest {
    private final Booking booking = new Booking(
            1L,
            LocalDateTime.of(2024, 1, 1, 1, 1, 1),
            LocalDateTime.of(2024, 1, 2, 1, 1, 1),
            new Item(1L, "Корректный предмет", "Корректное описание", true, 1L, 1L),
            new User(1L, "e5k4p3", "e5k4p3@gmail.com"),
            WAITING
    );

    @Test
    public void toBookingDtoResponseTest() {
        BookingDtoResponse bookingDtoResponse = BookingMapper.toBookingDtoResponse(booking);

        assertEquals(booking.getId(), bookingDtoResponse.getId());
        assertEquals(booking.getStart(), bookingDtoResponse.getStart());
        assertEquals(booking.getEnd(), bookingDtoResponse.getEnd());
        assertEquals(booking.getItem(), bookingDtoResponse.getItem());
        assertEquals(booking.getBooker(), bookingDtoResponse.getBooker());
        assertEquals(booking.getStatus(), bookingDtoResponse.getStatus());
    }

    @Test
    public void toBookingDtoTest() {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getItem().getId(), bookingDto.getItemId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBookerId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    public void toBookingTest() {
        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 2, 1, 1, 1),
                1L,
                1L,
                WAITING
        );

        Booking newBooking = BookingMapper.toBooking(bookingDto);

        assertEquals(bookingDto.getId(), newBooking.getId());
        assertEquals(bookingDto.getStart(), newBooking.getStart());
        assertEquals(bookingDto.getEnd(), newBooking.getEnd());
        assertNull(newBooking.getItem());
        assertNull(newBooking.getBooker());
        assertEquals(bookingDto.getStatus(), newBooking.getStatus());
    }
}
