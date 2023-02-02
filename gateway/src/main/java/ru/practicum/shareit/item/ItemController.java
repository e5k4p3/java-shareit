package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.ValidationErrorsHandler;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody ItemDto itemDto,
                                          BindingResult bindingResult) {
        ValidationErrorsHandler.logValidationErrors(bindingResult);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> deleteItem(@PathVariable Long itemId,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.deleteItem(itemId, userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.getAllItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItemsByText(@RequestParam String text,
                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.getItemsByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentDto commentDto,
                                 @PathVariable Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 BindingResult bindingResult) {
        ValidationErrorsHandler.logValidationErrors(bindingResult);
        return itemClient.addComment(commentDto, itemId, userId);
    }
}