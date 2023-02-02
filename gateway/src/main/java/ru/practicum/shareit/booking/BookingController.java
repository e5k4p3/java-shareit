package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.validation.ValidationErrorsHandler;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addBooking(@Valid @RequestBody BookingDto bookingDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             BindingResult bindingResult) {
        ValidationErrorsHandler.logValidationErrors(bindingResult);
        return bookingClient.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@PathVariable Long bookingId,
                                                @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam Boolean approved) {
        return bookingClient.updateBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(name = "state", defaultValue = "ALL", required = false) String state,
                                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        return bookingClient.getAllBookingsByUserId(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(name = "state", defaultValue = "ALL", required = false) String state,
                                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        return bookingClient.getAllBookingsByOwnerId(userId, bookingState, from, size);
    }
}