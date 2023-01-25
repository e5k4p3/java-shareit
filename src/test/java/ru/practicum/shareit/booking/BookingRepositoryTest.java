package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.PageableFactory;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.model.enums.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.enums.BookingStatus.WAITING;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User firstUser;
    private User secondUser;
    private Item firstItem;
    private Item secondItem;
    private Booking firstBooking;
    private Booking secondBooking;
    private Pageable pageable;
    private LocalDateTime date;

    @BeforeEach
    public void beforeEach() {
        firstUser = userRepository.save(new User(1L, "Имя первого", "first@email.com"));
        entityManager.persist(firstUser);
        secondUser = userRepository.save(new User(2L, "Имя второго", "second@email.com"));
        entityManager.persist(secondUser);
        firstItem = itemRepository.save(new Item(
                        1L,
                        "Название первого",
                        "Описание первого",
                        true,
                        2L,
                        null
                )
        );
        entityManager.persist(firstItem);
        secondItem = itemRepository.save(new Item(
                        2L,
                        "Название второго",
                        "Описание второго",
                        true,
                        2L,
                        null
                )
        );
        entityManager.persist(secondItem);
        firstBooking = bookingRepository.save(new Booking(
                        1L,
                        LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                        LocalDateTime.of(2024, 1, 3, 1, 1, 1),
                        firstItem,
                        firstUser,
                        WAITING
                )
        );
        entityManager.persist(firstBooking);
        secondBooking = bookingRepository.save(new Booking(
                        2L,
                        LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                        LocalDateTime.of(2024, 1, 3, 1, 1, 1),
                        secondItem,
                        firstUser,
                        WAITING
                )
        );
        entityManager.persist(secondBooking);
        entityManager.getEntityManager().getTransaction().commit();
        pageable = PageableFactory.makePage(0, 10);
        date = LocalDateTime.of(2024, 1, 2, 1, 1, 1);
    }

    @AfterEach
    public void afterEach() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    public void findByBookerIdAndItemIdAndEndIsBeforeTest() {
        Booking booking = bookingRepository.findByBookerIdAndItemIdAndEndIsBefore(1L, 1L, date.plusDays(2));

        assertEquals(1L, booking.getBooker().getId());
        assertEquals(1L, booking.getItem().getId());
        assertTrue(booking.getEnd().isBefore(date.plusDays(2)));
    }

    @Test
    public void findByItemIdAndEndBeforeAndStatusNotTest() {
        Booking booking = bookingRepository.findByItemIdAndEndBeforeAndStatusNot(1L, date.plusDays(2), APPROVED);

        assertEquals(1L, booking.getItem().getId());
        assertTrue(booking.getEnd().isBefore(date.plusDays(2)));
        assertNotEquals(APPROVED, booking.getStatus());
    }

    @Test
    public void findByItemIdAndStartAfterAndStatusTest() {
        Booking booking = bookingRepository.findByItemIdAndStartAfterAndStatus(1L, date.minusDays(2), WAITING);

        assertEquals(1L, booking.getItem().getId());
        assertTrue(booking.getStart().isAfter(date.minusDays(2)));
        assertEquals(WAITING, booking.getStatus());
    }

    @Test
    public void findAllByBookerIdTest() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(1L, pageable);

        assertEquals(2, bookings.size());
    }

    @Test
    public void findAllByBookerIdAndStartBeforeAndEndAfterTest() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                1L,
                date,
                date,
                pageable
        );

        assertEquals(2, bookings.size());
    }

    @Test
    public void findAllByBookerIdAndStartAfterTest() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(1L, date.minusDays(2), pageable);

        assertEquals(2, bookings.size());
    }

    @Test
    public void findAllByBookerIdAndEndBeforeTest() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(1L, date.plusDays(2), pageable);

        assertEquals(2, bookings.size());
    }

    @Test
    public void findAllByBookerIdAndStatusEqualsTest() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(1L, WAITING, pageable);

        assertEquals(2, bookings.size());
    }

    @Test
    public void findAllByItemOwnerTest() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerOrderByStartDesc(2L, pageable);

        assertEquals(2, bookings.size());
    }

    @Test
    public void findAllByItemOwnerAndStartAfterTest() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndStartAfterOrderByStartDesc(2L, date.minusDays(2), pageable);

        assertEquals(2, bookings.size());
    }

    @Test
    public void findAllByItemOwnerAndEndBeforeTest() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndEndBeforeOrderByStartDesc(2L, date.plusDays(2), pageable);

        assertEquals(2, bookings.size());
    }

    @Test
    public void findAllByItemOwnerAndStartBeforeAndEndAfterTest() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(2L, date, date, pageable);

        assertEquals(2, bookings.size());
    }

    @Test
    public void findAllByItemOwnerAndStatusEqualsTest() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndStatusEquals(2L, WAITING, pageable);

        assertEquals(2, bookings.size());
    }
}
