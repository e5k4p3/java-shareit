package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.util.ItemMapper;
import ru.practicum.shareit.validation.ValidationErrors;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") int userId,
                           @Valid @RequestBody ItemDto itemDto,
                           BindingResult bindingResult) {
        ValidationErrors.logValidationErrors(bindingResult);
        return ItemMapper.toItemDto(itemService.addItem(userId, ItemMapper.toItem(itemDto)));
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@PathVariable int itemId,
                              @RequestHeader("X-Sharer-User-Id") int userId,
                              @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.updateItem(itemId, userId, ItemMapper.toItem(itemDto)));
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteItem(@PathVariable int itemId,
                           @RequestHeader("X-Sharer-User-Id") int userId) {
        itemService.deleteItem(itemId, userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItemById(@PathVariable int itemId) {
        return ItemMapper.toItemDto(itemService.getItemById(itemId));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getAllItemsByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getItemsByText(@RequestParam String text) {
        return itemService.getItemsByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
