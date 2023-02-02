package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.util.ItemMapper;

import static org.junit.jupiter.api.Assertions.*;

public class ItemMapperTest {

    private final Item item = new Item(1L, "Название", "Описание", true, 1L, 1L);

    @Test
    public void toItemDtoTest() {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertEquals(1L, itemDto.getId());
        assertEquals("Название", itemDto.getName());
        assertEquals("Описание", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(1L, itemDto.getOwner());
        assertEquals(1L, itemDto.getRequestId());
    }

    @Test
    public void toItemDtoWithBookingTest() {
        ItemDtoWithBooking itemDto = ItemMapper.toItemDtoWithBooking(item);

        assertEquals(1L, itemDto.getId());
        assertEquals("Название", itemDto.getName());
        assertEquals("Описание", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(1L, itemDto.getOwner());
        assertEquals(1L, itemDto.getRequestId());
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
        assertNull(itemDto.getComments());
    }

    @Test
    public void toItemTest() {
        ItemDto itemDto = new ItemDto(1L, "Название", "Описание", true, 1L, 1L);

        Item item = ItemMapper.toItem(itemDto);

        assertEquals(1L, item.getId());
        assertEquals("Название", item.getName());
        assertEquals("Описание", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(1L, item.getOwner());
        assertEquals(1L, item.getRequestId());
    }
}
