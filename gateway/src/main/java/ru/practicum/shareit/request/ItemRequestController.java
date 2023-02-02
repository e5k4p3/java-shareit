package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.validation.ValidationErrorsHandler;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> addItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 BindingResult bindingResult) {
        ValidationErrorsHandler.logValidationErrors(bindingResult);
        return itemRequestService.addRequest(itemRequestDto, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllRequestsByUserId(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getRequestById(@PathVariable Long requestId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getRequestById(requestId, userId);
    }
}