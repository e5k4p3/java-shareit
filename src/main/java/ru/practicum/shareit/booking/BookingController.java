package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.util.BookingMapper;
import ru.practicum.shareit.validation.ValidationErrors;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDtoResponse addBooking(@Valid @RequestBody BookingDto bookingDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId,
                                         BindingResult bindingResult) {
        ValidationErrors.logValidationErrors(bindingResult);
        return BookingMapper.toBookingDtoResponse(bookingService.addBooking(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse updateBooking(@PathVariable Long bookingId,
                                            @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam Boolean approved) {
        return BookingMapper.toBookingDtoResponse(bookingService.updateBooking(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(@PathVariable Long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return BookingMapper.toBookingDtoResponse(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public List<BookingDtoResponse> getAllBookingsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam(name = "state", defaultValue = "ALL", required = false) String state) {
        return bookingService.getAllBookingsByUserId(userId, state).stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllBookingsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(name = "state", defaultValue = "ALL", required = false) String state) {
        return bookingService.getAllBookingsByOwnerId(userId, state).stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }
}
