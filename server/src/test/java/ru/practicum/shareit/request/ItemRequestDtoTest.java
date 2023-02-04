package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    JacksonTester<ItemRequestDto> jtItemRequestDto;
    @Autowired
    JacksonTester<ItemRequestDtoResponse> jtItemRequestDtoResponse;
    @Autowired
    JacksonTester<ItemRequestDtoWithItems> jtItemRequestDtoWithItems;

    @Test
    public void itemRequestDtoJsonTest() throws IOException {
        ItemRequestDto requestDto = new ItemRequestDto(1L, "Описание");

        JsonContent<ItemRequestDto> requestDtoResult = jtItemRequestDto.write(requestDto);

        assertThat(requestDtoResult).extractingJsonPathNumberValue("$.id").isEqualTo(requestDto.getId().intValue());
        assertThat(requestDtoResult).extractingJsonPathStringValue("$.description").isEqualTo(requestDto.getDescription());
    }

    @Test
    public void itemRequestDtoResponseTest() throws IOException {
        ItemRequestDtoResponse requestDto = new ItemRequestDtoResponse(
                1L,
                "Описание",
                LocalDateTime.of(2024, 1, 1, 1, 1, 1)
        );

        JsonContent<ItemRequestDtoResponse> requestDtoResult = jtItemRequestDtoResponse.write(requestDto);

        assertThat(requestDtoResult).extractingJsonPathNumberValue("$.id").isEqualTo(requestDto.getId().intValue());
        assertThat(requestDtoResult).extractingJsonPathStringValue("$.description").isEqualTo(requestDto.getDescription());
        assertThat(requestDtoResult).extractingJsonPathValue("$.created").isEqualTo(requestDto.getCreated().toString());
    }

    @Test
    public void itemRequestDtoWithItems() throws IOException {
        ItemRequestDtoWithItems requestDto = new ItemRequestDtoWithItems(
                1L,
                "Описание",
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                null
        );

        JsonContent<ItemRequestDtoWithItems> requestDtoResult = jtItemRequestDtoWithItems.write(requestDto);

        assertThat(requestDtoResult).extractingJsonPathNumberValue("$.id").isEqualTo(requestDto.getId().intValue());
        assertThat(requestDtoResult).extractingJsonPathStringValue("$.description").isEqualTo(requestDto.getDescription());
        assertThat(requestDtoResult).extractingJsonPathValue("$.created").isEqualTo(requestDto.getCreated().toString());
        assertThat(requestDtoResult).extractingJsonPathValue("$.items").isNull();
    }
}
