package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.booking.util.BookingMapper;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.model.enums.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.enums.BookingStatus.REJECTED;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookingServiceTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    private User firstUser;
    private User secondUser;
    private Item item;
    private Booking booking;
    private final EntityNotFoundException notFoundException = new EntityNotFoundException("Не найдено.");

    @BeforeEach
    public void beforeEach() {
        firstUser = new User(1L, "Имя первого", "first@email.com");
        secondUser = new User(2L, "Имя второго", "second@email.com");
        item = new Item(1L, "Название", "Описание", true, 2L, 1L);
        booking = new Booking(
                1L,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 2, 1, 1, 1),
                item,
                firstUser,
                APPROVED
        );
    }

    @Test
    public void addBookingTestSuccess() {
        when(itemService.getItemById(Mockito.anyLong())).thenReturn(item);
        when(bookingRepository.save(any())).thenReturn(booking);

        Booking addedBooking = bookingService.addBooking(BookingMapper.toBookingDto(booking), 1L);

        assertEquals(booking.getId(), addedBooking.getId());
        assertEquals(booking.getStart(), addedBooking.getStart());
        assertEquals(booking.getEnd(), addedBooking.getEnd());
        assertEquals(booking.getItem(), addedBooking.getItem());
        assertEquals(booking.getBooker(), addedBooking.getBooker());
        assertEquals(booking.getStatus(), addedBooking.getStatus());
    }

    @Test
    public void addBookingUnavailableItemTestFail() {
        item.setAvailable(false);
        when(itemService.getItemById(Mockito.anyLong())).thenReturn(item);

        assertThrows(EntityAvailabilityException.class, () -> bookingService.addBooking(BookingMapper.toBookingDto(booking), 1L));
    }

    @Test
    public void addBookingSelfBookingTestFail() {
        when(itemService.getItemById(Mockito.anyLong())).thenReturn(item);

        assertThrows(ForbiddenAccessException.class, () -> bookingService.addBooking(BookingMapper.toBookingDto(booking), 2L));
    }

    @Test
    public void addBookingEndIsBeforeStartTestFail() {
        booking.setEnd(booking.getStart().minusDays(1));
        when(itemService.getItemById(Mockito.anyLong())).thenReturn(item);

        assertThrows(ValidationException.class, () -> bookingService.addBooking(BookingMapper.toBookingDto(booking), 1L));
    }

    @Test
    public void addBookingItemNotFoundTestFail() {
        when(itemService.getItemById(Mockito.anyLong())).thenThrow(notFoundException);

        assertThrows(EntityNotFoundException.class, () -> bookingService.addBooking(BookingMapper.toBookingDto(booking), 1L));
    }

    @Test
    public void addBookingUserNotFoundTestFail() {
        when(userService.getUserById(Mockito.anyLong())).thenThrow(notFoundException);
        when(itemService.getItemById(Mockito.anyLong())).thenReturn(item);

        assertThrows(EntityNotFoundException.class, () -> bookingService.addBooking(BookingMapper.toBookingDto(booking), 999L));
    }

    @Test
    public void updateBookingTestSuccess() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(secondUser);
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        Booking updatedBooking = bookingService.updateBooking(1L, 2L, false);

        assertEquals(updatedBooking.getStatus(), REJECTED);
    }

    @Test
    public void updateBookingNotFoundTestFail() {
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.updateBooking(1L, 2L, false));
    }

    @Test
    public void updateBookingUserNotFoundTestFail() {
        when(userService.getUserById(Mockito.anyLong())).thenThrow(notFoundException);

        assertThrows(EntityNotFoundException.class, () -> bookingService.updateBooking(1L, 999L, false));
    }

    @Test
    public void updateBookingForbiddenAccessTestFail() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(firstUser);
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenAccessException.class, () -> bookingService.updateBooking(1L, 1L, false));
    }

    @Test
    public void updateBookingAlreadyApprovedTestFail() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(secondUser);
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class, () -> bookingService.updateBooking(1L, 2L, true));
    }

    @Test
    public void getBookingByIdTestSuccess() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(secondUser);
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));
        when(itemService.getItemById(Mockito.anyLong())).thenReturn(item);

        assertEquals(1L, bookingService.getBookingById(1L, 2L).getId());
    }

    @Test
    public void getBookingByIdNotFoundTestFail() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(secondUser);
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBookingById(1L, 2L));
    }

    @Test
    public void getBookingByIdUserNotFoundTestFail() {
        when(userService.getUserById(Mockito.anyLong())).thenThrow(notFoundException);

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBookingById(1L, 999L));
    }

    @Test
    public void getBookingByIdForbiddenAccess() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(new User(3L, "Имя третьего", "third@email.com"));
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));
        when(itemService.getItemById(Mockito.anyLong())).thenReturn(item);

        assertThrows(ForbiddenAccessException.class, () -> bookingService.getBookingById(1L, 3L));
    }

    @Test
    public void getAllBookingsByUserIdTestSuccess() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(firstUser);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(Mockito.anyLong(), any())).thenReturn(List.of(booking));

        List<Booking> bookings = bookingService.getAllBookingsByUserId(1L, "ALL", 0, 10);

        assertEquals(1, bookings.size());
    }

    @Test
    public void getAllBookingsByUserIdNotFoundTestFail() {
        when(userService.getUserById(Mockito.anyLong())).thenThrow(notFoundException);

        assertThrows(EntityNotFoundException.class, () -> bookingService.getAllBookingsByUserId(1L, "ALL", 0, 10));
    }

    @Test
    public void getAllBookingsByUserIdUnsupportedStateTestFail() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(firstUser);

        assertThrows(UnsupportedStateException.class, () -> bookingService.getAllBookingsByUserId(1L, "UNSUPPORTED", 0, 10));
    }

    @Test
    public void getAllBookingsByOwnerIdTestSuccess() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(secondUser);
        when(bookingRepository.findAllByItemOwnerOrderByStartDesc(Mockito.anyLong(), any())).thenReturn(List.of(booking));

        List<Booking> bookings = bookingService.getAllBookingsByOwnerId(2L, "ALL", 0, 10);

        assertEquals(1, bookings.size());
    }

    @Test
    public void getAllBookingsByOwnerIdNotFoundTestFail() {
        when(userService.getUserById(Mockito.anyLong())).thenThrow(notFoundException);

        assertThrows(EntityNotFoundException.class, () -> bookingService.getAllBookingsByOwnerId(2L, "ALL", 0, 10));
    }

    @Test
    public void getAllBookingsByOwnerIdUnsupportedStateTestFail() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(secondUser);

        assertThrows(UnsupportedStateException.class, () -> bookingService.getAllBookingsByOwnerId(2L, "UNSUPPORTED", 0, 10));
    }
}
