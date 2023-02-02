package ru.practicum.shareit.request.util;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;

@UtilityClass
public final class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                null,
                LocalDateTime.now()
        );
    }

    public static ItemRequestDtoWithItems toItemRequestDtoWithItems(ItemRequest itemRequest) {
        return new ItemRequestDtoWithItems(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                new ArrayList<>()
        );
    }

    public static ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest itemRequest) {
        return new ItemRequestDtoResponse(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }
}
