package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findByBookerIdAndItemIdAndEndIsBefore(Long userId, Long itemId, LocalDateTime time);

    Booking findByItemIdAndEndBeforeAndStatusNot(Long itemId, LocalDateTime end, BookingStatus status);

    Booking findByItemIdAndStartAfterAndStatus(Long itemId, LocalDateTime start, BookingStatus status);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusEqualsOrderByStartDesc(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemOwnerOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItemOwnerAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStatusEquals(Long userId, BookingStatus status, Pageable pageable);
}
