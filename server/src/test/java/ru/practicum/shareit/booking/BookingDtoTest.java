package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    JacksonTester<BookingDto> jtBookingDto;
    @Autowired
    JacksonTester<BookingDtoResponse> jtBookingDtoResponse;

    @Test
    public void bookingDtoJsonTest() throws IOException {
        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 2, 1, 1, 1),
                1L,
                1L,
                null
        );

        JsonContent<BookingDto> bookingDtoResult = jtBookingDto.write(bookingDto);

        assertThat(bookingDtoResult).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDto.getId().intValue());
        assertThat(bookingDtoResult).extractingJsonPathValue("$.start").isEqualTo(bookingDto.getStart().toString());
        assertThat(bookingDtoResult).extractingJsonPathValue("$.end").isEqualTo(bookingDto.getEnd().toString());
        assertThat(bookingDtoResult).extractingJsonPathNumberValue("$.itemId").isEqualTo(bookingDto.getItemId().intValue());
        assertThat(bookingDtoResult).extractingJsonPathNumberValue("$.bookerId").isEqualTo(bookingDto.getBookerId().intValue());
        assertThat(bookingDtoResult).extractingJsonPathValue("$.status").isNull();
    }

    @Test
    public void bookingDtoResponseTest() throws IOException {
        BookingDtoResponse bookingDto = new BookingDtoResponse(
                1L,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 2, 1, 1, 1),
                null,
                null,
                null
        );

        JsonContent<BookingDtoResponse> bookingDtoResult = jtBookingDtoResponse.write(bookingDto);

        assertThat(bookingDtoResult).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDto.getId().intValue());
        assertThat(bookingDtoResult).extractingJsonPathValue("$.start").isEqualTo(bookingDto.getStart().toString());
        assertThat(bookingDtoResult).extractingJsonPathValue("$.end").isEqualTo(bookingDto.getEnd().toString());
        assertThat(bookingDtoResult).extractingJsonPathValue("$.item").isNull();
        assertThat(bookingDtoResult).extractingJsonPathValue("$.booker").isNull();
        assertThat(bookingDtoResult).extractingJsonPathValue("$.status").isNull();
    }
}
