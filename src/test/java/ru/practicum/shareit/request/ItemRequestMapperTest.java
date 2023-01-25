package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.util.ItemRequestMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ItemRequestMapperTest {
    ItemRequest itemRequest = new ItemRequest(
            1L,
            "Описание",
            new User(1L, "Имя", "email@email.com"),
            LocalDateTime.of(2024, 1, 1, 1, 1, 1)
    );

    @Test
    public void toItemRequestDtoResponseTest() {
        ItemRequestDtoResponse itemRequestDto = ItemRequestMapper.toItemRequestDtoResponse(itemRequest);

        assertEquals(1L, itemRequestDto.getId());
        assertEquals("Описание", itemRequestDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());
    }

    @Test
    public void toItemRequestDtoWithItemsTest() {
        ItemRequestDtoWithItems itemRequestDto = ItemRequestMapper.toItemRequestDtoWithItems(itemRequest);

        assertEquals(1L, itemRequestDto.getId());
        assertEquals("Описание", itemRequestDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());
        assertNotNull(itemRequestDto.getItems());
    }

    @Test
    public void toItemRequestTest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Описание");

        ItemRequest newItemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);

        assertEquals(1L, newItemRequest.getId());
        assertEquals("Описание", newItemRequest.getDescription());
        assertNull(newItemRequest.getRequester());
        assertEquals(LocalDateTime.now().toLocalDate(), newItemRequest.getCreated().toLocalDate());
    }
}
