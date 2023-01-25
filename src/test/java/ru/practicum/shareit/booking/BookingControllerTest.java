package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ForbiddenAccessException;
import ru.practicum.shareit.exceptions.UnsupportedStateException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.model.enums.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.enums.BookingStatus.WAITING;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
    private Booking booking;
    private BookingDto correctBooking;
    private BookingDto bookingWithStartIsNull;
    private BookingDto bookingWithEndIsNull;
    private BookingDto bookingWithStartInPast;
    private BookingDto bookingWithEndInPast;
    private BookingDto bookingWithStartAfterEnd;
    private BookingDto bookingWithoutExistingItem;
    private final ValidationException validationException = new ValidationException("Валидация.");
    private final EntityNotFoundException notFoundException = new EntityNotFoundException("Не найдено.");
    private final ForbiddenAccessException forbiddenAccessException = new ForbiddenAccessException("Запрещено.");
    private final IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Недоступно.");
    private final UnsupportedStateException unsupportedStateException = new UnsupportedStateException("Неподдерживаемый.");

    @BeforeEach
    public void beforeEach() {
        booking = new Booking(
                1L,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 2, 1, 1, 1),
                new Item(1L, "Корректный предмет", "Корректное описание", true, 1L, 1L),
                new User(1L, "e5k4p3", "e5k4p3@gmail.com"),
                WAITING
        );
        correctBooking = new BookingDto(
                1L,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 2, 1, 1, 1),
                1L,
                null,
                null
        );
        bookingWithStartIsNull = new BookingDto(
                2L,
                null,
                LocalDateTime.of(2024, 1, 2, 1, 1, 1),
                1L,
                null,
                null
        );
        bookingWithEndIsNull = new BookingDto(
                3L,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                null,
                1L,
                null,
                null
        );
        bookingWithStartInPast = new BookingDto(
                4L,
                LocalDateTime.of(2000, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 2, 1, 1, 1),
                1L,
                null,
                null
        );
        bookingWithEndInPast = new BookingDto(
                5L,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                LocalDateTime.of(2000, 1, 2, 1, 1, 1),
                1L,
                null,
                null
        );
        bookingWithStartAfterEnd = new BookingDto(
                6L,
                LocalDateTime.of(2024, 1, 2, 1, 1, 1),
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                1L,
                null,
                null
        );
        bookingWithoutExistingItem = new BookingDto(
                7L,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 2, 1, 1, 1),
                999L,
                null,
                null
        );
    }

    @Test
    public void validationTestSuccess() throws Exception {
        when(bookingService.addBooking(any(), Mockito.anyLong())).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctBooking))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId().intValue())))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.status", is(WAITING.toString())));
    }

    @Test
    public void validationTestFail() throws Exception {
        when(bookingService.addBooking(any(), Mockito.anyLong())).thenThrow(validationException);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingWithStartIsNull))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingWithEndIsNull))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingWithStartInPast))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingWithEndInPast))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingWithStartAfterEnd))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Ошибка валидации.")));
    }

    @Test
    public void addBookingNotFoundTestFail() throws Exception {
        when(bookingService.addBooking(any(), Mockito.anyLong())).thenThrow(notFoundException);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctBooking))
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Объект не найден.")));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingWithoutExistingItem))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Объект не найден.")));
    }

    @Test
    public void addBookingForbiddenAccessTestFail() throws Exception {
        when(bookingService.addBooking(any(), Mockito.anyLong())).thenThrow(forbiddenAccessException);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctBooking))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Отсутствие прав доступа.")));
    }

    @Test
    public void addBookingNoHeaderTestFail() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctBooking)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void updateBookingTestSuccess() throws Exception {
        booking.setStatus(APPROVED);

        when(bookingService.updateBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(booking);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId().intValue())))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.status", is(APPROVED.toString())));
    }

    @Test
    public void updateBookingNotFoundTestFail() throws Exception {
        when(bookingService.updateBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean())).thenThrow(notFoundException);

        mockMvc.perform(patch("/bookings/999?approved=true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Объект не найден.")));

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Объект не найден.")));
    }

    @Test
    public void updateBookingForbiddenAccessTestFail() throws Exception {
        when(bookingService.updateBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean())).thenThrow(forbiddenAccessException);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Отсутствие прав доступа.")));
    }

    @Test
    public void updateBookingAlreadyApprovedTestFail() throws Exception {
        when(bookingService.updateBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean())).thenThrow(illegalArgumentException);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Недопустимый аргумент.")));
    }

    @Test
    public void updateBookingWithoutHeaderTestFail() throws Exception {
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctBooking)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void getBookingByIdTestSuccess() throws Exception {
        when(bookingService.getBookingById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(booking);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId().intValue())))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.status", is(WAITING.toString())));
    }

    @Test
    public void getBookingByIdNotFoundTestFail() throws Exception {
        when(bookingService.getBookingById(Mockito.anyLong(), Mockito.anyLong())).thenThrow(notFoundException);

        mockMvc.perform(get("/bookings/999")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Объект не найден.")));

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Объект не найден.")));
    }

    @Test
    public void getBookingByIdForbiddenAccessTestFail() throws Exception {
        when(bookingService.getBookingById(Mockito.anyLong(), Mockito.anyLong())).thenThrow(forbiddenAccessException);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Отсутствие прав доступа.")));
    }

    @Test
    public void getBookingByIdWithoutHeaderTestFail() throws Exception {
        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void getAllBookingsByUserIdTestSuccess() throws Exception {
        when(bookingService.getAllBookingsByUserId(Mockito.anyLong(), any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void getAllBookingsByUserIdNotFoundTestFail() throws Exception {
        when(bookingService.getAllBookingsByUserId(Mockito.anyLong(), any(), Mockito.anyInt(), Mockito.anyInt())).thenThrow(notFoundException);

        mockMvc.perform(get("/bookings?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Объект не найден.")));
    }

    @Test
    public void getAllBookingsByUserIdUnsupportedStateTestFail() throws Exception {
        when(bookingService.getAllBookingsByUserId(Mockito.anyLong(), any(), Mockito.anyInt(), Mockito.anyInt())).thenThrow(unsupportedStateException);

        mockMvc.perform(get("/bookings?state=UNSUPPORTED&from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.description", is("Необрабатываемый аргумент.")));
    }

    @Test
    public void getAllBookingsByUserIdWithoutHeader() throws Exception {
        mockMvc.perform(get("/bookings?state=ALL&from=0&size=10"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void getAllBookingsByOwnerIdTestSuccess() throws Exception {
        when(bookingService.getAllBookingsByOwnerId(Mockito.anyLong(), any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void getAllBookingsByOwnerIdNotFoundTestFail() throws Exception {
        when(bookingService.getAllBookingsByOwnerId(Mockito.anyLong(), any(), Mockito.anyInt(), Mockito.anyInt())).thenThrow(notFoundException);

        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Объект не найден.")));
    }

    @Test
    public void getAllBookingsByOwnerIdUnsupportedStateTestFail() throws Exception {
        when(bookingService.getAllBookingsByOwnerId(Mockito.anyLong(), any(), Mockito.anyInt(), Mockito.anyInt())).thenThrow(unsupportedStateException);

        mockMvc.perform(get("/bookings/owner?state=UNSUPPORTED&from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.description", is("Необрабатываемый аргумент.")));
    }

    @Test
    public void getAllBookingsByOwnerIdWithoutHeader() throws Exception {
        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=10"))
                .andExpect(status().is4xxClientError());
    }
}
