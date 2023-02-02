package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.util.ItemRequestMapper;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDtoResponse addItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.addRequest(ItemRequestMapper.toItemRequest(itemRequestDto), userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDtoWithItems> getAllRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllRequestsByUserId(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDtoWithItems> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDtoWithItems getRequestById(@PathVariable Long requestId,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getRequestById(requestId, userId);
    }
}