package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findByBookerIdAndItemIdAndEndIsBefore(Long userId, Long itemId, LocalDateTime time);

    Booking findByItemIdAndEndBeforeAndStatusNot(Long itemId, LocalDateTime end, BookingStatus status);

    Booking findByItemIdAndStartAfterAndStatus(Long itemId, LocalDateTime start, BookingStatus status);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStatusEqualsOrderByStartDesc(Long userId, BookingStatus status);

    List<Booking> findAllByItemOwnerOrderByStartDesc(Long userId);

    List<Booking> findAllByItemOwnerAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start);

    List<Booking> findAllByItemOwnerAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerAndStatusEquals(Long userId, BookingStatus status);
}
