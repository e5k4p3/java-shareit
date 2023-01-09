package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.util.BookingMapper;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.model.enums.BookingStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public Booking addBooking(BookingDto bookingDto, Long userId) {
        checkItemAvailability(bookingDto.getItemId());
        checkItemOwnerForSelfBooking(bookingDto.getItemId(), userId);
        checkBookingEnd(bookingDto);
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(userService.getUserById(userId));
        booking.setItem(itemService.getItemById(bookingDto.getItemId()));
        booking.setStatus(WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking updateBooking(Long bookingId, Long userId, Boolean status) {
        userService.getUserById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new EntityNotFoundException("Бронь с id " + bookingId + " не найдена."));
        checkItemOwner(booking, userId);
        if (booking.getStatus().equals(WAITING) && status) {
            booking.setStatus(APPROVED);
        } else if (booking.getStatus().equals(WAITING) && !status) {
            booking.setStatus(REJECTED);
        } else if (booking.getStatus().equals(APPROVED) && status) {
            throw new IllegalArgumentException("Бронирование уже одобрено.");
        } else if (booking.getStatus().equals(APPROVED) && !status) {
            booking.setStatus(REJECTED);
        }
        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingById(Long bookingId, Long userId) {
        userService.getUserById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new EntityNotFoundException("Бронь с id " + bookingId + " не найдена."));
        Item item = itemService.getItemById(booking.getItem().getId());
        if (booking.getBooker().getId() == userId || item.getOwner() == userId) {
            return booking;
        } else {
            throw new ForbiddenAccessException("Пользователь с id " + userId +
                    " не имеет прав доступа к изменению бронирования с id " + booking.getId() + ".");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllBookingsByUserId(Long userId, String state) {
        userService.getUserById(userId);
        BookingState stateEnum = checkState(state);
        switch (stateEnum) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(userId, WAITING);
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(userId, REJECTED);
            default:
                throw new UnsupportedOperationException("Недоступная операция.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllBookingsByOwnerId(Long userId, String state) {
        userService.getUserById(userId);
        BookingState stateEnum = checkState(state);
        switch (stateEnum) {
            case ALL:
                return bookingRepository.findAllByItemOwnerOrderByStartDesc(userId);
            case CURRENT:
                return bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findAllByItemOwnerAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            case PAST:
                return bookingRepository.findAllByItemOwnerAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findAllByItemOwnerAndStatusEquals(userId, WAITING);
            case REJECTED:
                return bookingRepository.findAllByItemOwnerAndStatusEquals(userId, REJECTED);
            default:
                throw new UnsupportedOperationException("Недоступная операция.");
        }
    }

    private void checkItemOwner(Booking booking, Long userId) {
        if (booking.getItem().getOwner() != userId) {
            throw new ForbiddenAccessException("Пользователь с id " + userId +
                    " не имеет прав доступа к изменению статуса бронирования с id " + booking.getId() + ".");
        }
    }

    private void checkItemOwnerForSelfBooking(Long itemId, Long userId) {
        if (itemService.getItemById(itemId).getOwner() == userId) {
            throw new ForbiddenAccessException("Пользователь не может забронировать собственный предмет.");
        }
    }

    private void checkItemAvailability(Long itemId) {
        if (!itemService.getItemById(itemId).getAvailable()) {
            throw new EntityAvailabilityException("Данный предмет недоступен для бронирования.");
        }
    }

    private void checkBookingEnd(BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("Время окончания бронирования не может быть раньше ее начала.");
        }
    }

    private BookingState checkState(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Unknown state: " + state);
        }
    }
}
