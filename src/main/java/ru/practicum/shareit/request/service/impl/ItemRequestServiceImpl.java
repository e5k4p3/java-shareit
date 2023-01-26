package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.util.ItemMapper;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.util.ItemRequestMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.PageableMaker;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public ItemRequestDtoResponse addRequest(ItemRequest itemRequest, Long userId) {
        itemRequest.setRequester(userService.getUserById(userId));
        ItemRequestDtoResponse request = ItemRequestMapper.toItemRequestDtoResponse(itemRequestRepository.save(itemRequest));
        log.info("Запрос c id " + request.getId() + " добавлен.");
        return request;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDtoWithItems> getAllRequestsByUserId(Long userId) {
        userService.getUserById(userId);
        List<ItemRequestDtoWithItems> allRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId).stream()
                .map(ItemRequestMapper::toItemRequestDtoWithItems)
                .collect(Collectors.toList());
        allRequests.forEach(i -> i.setItems(itemService.getAllItemsByRequestId(i.getId()).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList())));
        return allRequests;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDtoWithItems> getAllRequests(Long userId, Integer from, Integer size) {
        userService.getUserById(userId);
        List<ItemRequestDtoWithItems> allRequests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, PageableMaker.makePage(from, size)).stream()
                .map(ItemRequestMapper::toItemRequestDtoWithItems)
                .collect(Collectors.toList());
        allRequests.forEach(i -> i.setItems(itemService.getAllItemsByRequestId(i.getId()).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList())));
        return allRequests;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDtoWithItems getRequestById(Long requestId, Long userId) {
        userService.getUserById(userId);
        ItemRequestDtoWithItems request = ItemRequestMapper.toItemRequestDtoWithItems(itemRequestRepository.findById(requestId).orElseThrow(() ->
                new EntityNotFoundException("Запрос с id " + requestId + " не найден.")));
        request.setItems(itemService.getAllItemsByRequestId(requestId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
        return request;
    }
}
