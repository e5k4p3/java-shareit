package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(int userId, Item item);

    Item updateItem(int itemId, int userId, Item item);

    void deleteItem(int itemId, int userId);

    Item getItemById(int itemId);

    List<Item> getAllItemsByUserId(int userId);

    List<Item> getItemsByText(String text);
}
