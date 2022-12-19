package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Item addItem(int userId, Item item) {
        userRepository.getUserById(userId);
        return itemRepository.addItem(userId, item);
    }

    @Override
    public Item updateItem(int itemId, int userId, Item item) {
        userRepository.getUserById(userId);
        return itemRepository.updateItem(itemId, userId, item);
    }

    @Override
    public void deleteItem(int itemId, int userId) {
        userRepository.getUserById(userId);
        itemRepository.deleteItem(itemId, userId);
    }

    @Override
    public Item getItemById(int itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<Item> getAllItemsByUserId(int userId) {
        userRepository.getUserById(userId);
        return itemRepository.getAllItemsByUserId(userId);
    }

    @Override
    public List<Item> getItemsByText(String text) {
        return itemRepository.getItemsByText(text);
    }
}
