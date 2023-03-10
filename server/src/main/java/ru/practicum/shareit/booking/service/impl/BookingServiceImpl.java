package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.util.PageableMaker;

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
        log.info("Бронирование с id пользователя " + booking.getBooker().getId() + " и id предмета " + booking.getItem().getId() + " было добавлено.");
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
        log.info("У бронирования с id " + bookingId + " был изменен статус на " + booking.getStatus() + ".");
        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingById(Long bookingId, Long userId) {
        userService.getUserById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new EntityNotFoundException("Бронь с id " + bookingId + " не найдена."));
        Item item = itemService.getItemById(booking.getItem().getId());
        if (booking.getBooker().getId().equals(userId) || item.getOwner().equals(userId)) {
            return booking;
        } else {
            throw new ForbiddenAccessException("Пользователь с id " + userId +
                    " не имеет прав доступа к изменению бронирования с id " + booking.getId() + ".");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllBookingsByUserId(Long userId, String state, Integer from, Integer size) {
        userService.getUserById(userId);
        BookingState stateEnum = checkState(state);
        Pageable pageable = PageableMaker.makePage(from, size);
        switch (stateEnum) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageable);
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable);
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable);
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(userId, WAITING, pageable);
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(userId, REJECTED, pageable);
            default:
                throw new UnsupportedOperationException("Недоступная операция.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllBookingsByOwnerId(Long userId, String state, Integer from, Integer size) {
        userService.getUserById(userId);
        BookingState stateEnum = checkState(state);
        Pageable pageable = PageableMaker.makePage(from, size);
        switch (stateEnum) {
            case ALL:
                return bookingRepository.findAllByItemOwnerOrderByStartDesc(userId, pageable);
            case CURRENT:
                return bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
            case FUTURE:
                return bookingRepository.findAllByItemOwnerAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable);
            case PAST:
                return bookingRepository.findAllByItemOwnerAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable);
            case WAITING:
                return bookingRepository.findAllByItemOwnerAndStatusEquals(userId, WAITING, pageable);
            case REJECTED:
                return bookingRepository.findAllByItemOwnerAndStatusEquals(userId, REJECTED, pageable);
            default:
                throw new UnsupportedOperationException("Недоступная операция.");
        }
    }

    private void checkItemOwner(Booking booking, Long userId) {
        if (!booking.getItem().getOwner().equals(userId)) {
            throw new ForbiddenAccessException("Пользователь с id " + userId +
                    " не имеет прав доступа к изменению статуса бронирования с id " + booking.getId() + ".");
        }
    }

    private void checkItemOwnerForSelfBooking(Long itemId, Long userId) {
        if (itemService.getItemById(itemId).getOwner().equals(userId)) {
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
