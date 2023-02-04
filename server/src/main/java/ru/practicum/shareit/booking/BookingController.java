package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.util.BookingMapper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDtoResponse addBooking(@RequestBody BookingDto bookingDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получен POST запрос на добаление бронирования.");
        return BookingMapper.toBookingDtoResponse(bookingService.addBooking(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse updateBooking(@PathVariable Long bookingId,
                                            @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam Boolean approved) {
        log.debug("Получен PATCH запрос на изменения статуса бронирования.");
        return BookingMapper.toBookingDtoResponse(bookingService.updateBooking(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(@PathVariable Long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получен GET запрос на получение бронирования по id.");
        return BookingMapper.toBookingDtoResponse(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public List<BookingDtoResponse> getAllBookingsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam(name = "state", defaultValue = "ALL", required = false) String state,
                                                           @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                           @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.debug("Получен GET запрос на получение всех бронирований по id пользователя.");
        return bookingService.getAllBookingsByUserId(userId, state, from, size).stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllBookingsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(name = "state", defaultValue = "ALL", required = false) String state,
                                                            @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.debug("Получен GET запрос на получение всех бронирований по id владельца.");
        return bookingService.getAllBookingsByOwnerId(userId, state, from, size).stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }
}
