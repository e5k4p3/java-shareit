package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.util.ItemMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.addItem(userId, ItemMapper.toItem(itemDto)));
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.updateItem(itemId, userId, ItemMapper.toItem(itemDto)));
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteItem(@PathVariable Long itemId,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemService.deleteItem(itemId, userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDtoWithBooking getItemById(@PathVariable Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemDtoById(itemId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDtoWithBooking> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemService.getAllItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getItemsByText(@RequestParam String text,
                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemService.getItemsByText(text, from, size).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody CommentDto commentDto,
                                 @PathVariable Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addComment(commentDto, itemId, userId);
    }
}
