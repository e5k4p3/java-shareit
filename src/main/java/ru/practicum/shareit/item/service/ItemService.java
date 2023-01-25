package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(Long userId, Item item);

    Item updateItem(Long itemId, Long userId, Item item);

    void deleteItem(Long itemId, Long userId);

    Item getItemById(Long itemId);

    ItemDtoWithBooking getItemDtoById(Long itemId, Long userId);

    List<ItemDtoWithBooking> getAllItemsByUserId(Long userId, Integer from, Integer size);

    List<Item> getItemsByText(String text, Integer from, Integer size);

    List<Item> getAllItemsByRequestId(Long requestId);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);
}
