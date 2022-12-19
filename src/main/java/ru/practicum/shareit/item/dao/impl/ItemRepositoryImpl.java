package ru.practicum.shareit.item.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.IllegalEntityAccessException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, Item> allItems = new HashMap<>();
    private int id = 1;

    @Override
    public Item addItem(int userId, Item item) {
        item.setId(getNewId());
        item.setOwner(userId);
        allItems.put(item.getId(), item);
        log.info("Предмет с id " + item.getId() + " добавлен.");
        return item;
    }

    @Override
    public Item updateItem(int itemId, int userId, Item item) {
        checkItemExistence(itemId);
        checkItemOwner(itemId, userId);
        if (item.getName() != null && !item.getName().isBlank()) {
            allItems.get(itemId).setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            allItems.get(itemId).setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            allItems.get(itemId).setAvailable(item.getAvailable());
        }
        log.info("Предмет с id " + itemId + " обновлен.");
        return getItemById(itemId);
    }

    @Override
    public void deleteItem(int itemId, int userId) {
        checkItemExistence(itemId);
        checkItemOwner(itemId, userId);
        allItems.remove(itemId);
        log.info("Предмет с id " + itemId + " удален.");
    }

    @Override
    public Item getItemById(int itemId) {
        checkItemExistence(itemId);
        return allItems.get(itemId);
    }

    @Override
    public List<Item> getAllItemsByUserId(int userId) {
        return allItems.values().stream()
                .filter(i -> i.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemsByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return allItems.values().stream()
                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    private void checkItemExistence(int itemId) {
        if (!allItems.containsKey(itemId)) {
            throw new EntityNotFoundException("Предмет с id " + itemId + " не найден.");
        }
    }

    private void checkItemOwner(int itemId, int userId) {
        if (allItems.get(itemId).getOwner() != userId) {
            throw new IllegalEntityAccessException("Пользователь с id " + userId +
                    " не имеет прав доступа к изменению объекта с id " + itemId + ".");
        }
    }

    private int getNewId() {
        return id++;
    }
}
