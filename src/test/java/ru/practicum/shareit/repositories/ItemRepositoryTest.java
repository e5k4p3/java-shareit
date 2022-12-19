package ru.practicum.shareit.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.IllegalEntityAccessException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dao.impl.ItemRepositoryImpl;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemRepositoryTest {
    private ItemRepository itemRepository;
    private Item firstItem;
    private Item secondItem;
    private Item thirdItem;

    @BeforeEach
    public void beforeEach() {
        itemRepository = new ItemRepositoryImpl();
        firstItem = new Item(1, "Первый предмет", "Описание первого предмета", true, 1);
        secondItem = new Item(2, "Второй предмет", "Описание второго предмета", false, 1);
        thirdItem = new Item(3, "Третий предмет", "Описание третьего предмета", true, 2);
    }

    @Test
    public void addItem() {
        itemRepository.addItem(1, firstItem);
        assertEquals(firstItem, itemRepository.getItemById(1));
    }

    @Test
    public void updateItem() {
        itemRepository.addItem(1, firstItem);
        itemRepository.updateItem(1, 1, secondItem);
        Item updatedItem = itemRepository.getItemById(1);
        assertEquals(secondItem.getName(), updatedItem.getName());
        assertEquals(secondItem.getDescription(), updatedItem.getDescription());
        assertEquals(secondItem.getAvailable(), updatedItem.getAvailable());
        assertThrows(IllegalEntityAccessException.class, () -> itemRepository.updateItem(1, 999, thirdItem));
    }

    @Test
    public void deleteItem() {
        itemRepository.addItem(1, firstItem);
        itemRepository.addItem(1, secondItem);
        assertEquals(2, itemRepository.getAllItemsByUserId(1).size());
        itemRepository.deleteItem(1, 1);
        assertEquals(1, itemRepository.getAllItemsByUserId(1).size());
        assertThrows(IllegalEntityAccessException.class, () -> itemRepository.deleteItem(2, 999));
    }

    @Test
    public void getItemById() {
        itemRepository.addItem(1, firstItem);
        itemRepository.addItem(1, secondItem);
        assertEquals(firstItem, itemRepository.getItemById(1));
        assertEquals(secondItem, itemRepository.getItemById(2));
        assertThrows(EntityNotFoundException.class, () -> itemRepository.getItemById(999));
    }

    @Test
    public void getAllItemsByUserId() {
        itemRepository.addItem(1, firstItem);
        itemRepository.addItem(1, secondItem);
        itemRepository.addItem(2, thirdItem);
        assertEquals(2, itemRepository.getAllItemsByUserId(1).size());
    }

    @Test
    public void getItemByText() {
        itemRepository.addItem(1, firstItem);
        itemRepository.addItem(2, secondItem);
        itemRepository.addItem(3, thirdItem);
        assertEquals(2, itemRepository.getItemsByText("ПрЕдМеТ").size());
        assertEquals(0, itemRepository.getItemsByText("").size());
    }
}
