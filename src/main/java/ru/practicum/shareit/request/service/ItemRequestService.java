package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoResponse addRequest(ItemRequest itemRequest, Long userId);

    List<ItemRequestDtoWithItems> getAllRequestsByUserId(Long userId);

    List<ItemRequestDtoWithItems> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestDtoWithItems getRequestById(Long requestId, Long userId);

}
