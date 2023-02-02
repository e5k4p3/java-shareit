package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingMapper;
import ru.practicum.shareit.comment.dao.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.util.CommentMapper;
import ru.practicum.shareit.exceptions.EntityAvailabilityException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.IllegalEntityAccessException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.model.enums.BookingStatus.APPROVED;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    private User user;
    private Item item;
    private Booking booking;
    private Comment comment;
    private final EntityNotFoundException notFoundException = new EntityNotFoundException("Не найдено.");

    @BeforeEach
    public void beforeEach() {
        user = new User(1L, "Имя", "email@email.com");
        item = new Item(1L, "Название", "Описание", true, 1L, 1L);
        booking = new Booking(
                1L,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 2, 1, 1, 1),
                item,
                user,
                APPROVED
        );
        comment = new Comment(1L, "Текст", user, 1L, LocalDateTime.now());
    }

    @Test
    public void addItemTestSuccess() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(user);
        when(itemRepository.save(any())).thenReturn(item);

        Item addedItem = itemService.addItem(2L, item);

        assertEquals(item.getId(), addedItem.getId());
        assertEquals(item.getName(), addedItem.getName());
        assertEquals(item.getDescription(), addedItem.getDescription());
        assertEquals(item.getAvailable(), addedItem.getAvailable());
        assertEquals(2L, addedItem.getOwner());
        assertEquals(item.getRequestId(), addedItem.getRequestId());
    }

    @Test
    public void addItemUserNotFoundTestFail() {
        when(userService.getUserById(Mockito.anyLong())).thenThrow(notFoundException);

        assertThrows(EntityNotFoundException.class, () -> itemService.addItem(999L, item));
    }

    @Test
    public void updateItemTestSuccess() {
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        Item newItem = new Item(1L, "Новое название", "Новое описание", false, 1L, 1L);
        Item updatedItem = itemService.updateItem(1L, 1L, newItem);

        assertEquals(newItem.getId(), updatedItem.getId());
        assertEquals(newItem.getName(), updatedItem.getName());
        assertEquals(newItem.getDescription(), updatedItem.getDescription());
        assertEquals(newItem.getAvailable(), updatedItem.getAvailable());
        assertEquals(newItem.getOwner(), updatedItem.getOwner());
        assertEquals(newItem.getRequestId(), updatedItem.getRequestId());
    }

    @Test
    public void updateItemNotFoundTestFail() {
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(1L, 1L, item));
    }

    @Test
    public void updateItemUserNotOwnerTestFail() {
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        assertThrows(IllegalEntityAccessException.class, () -> itemService.updateItem(1L, 2L, item));
    }

    @Test
    public void deleteItemTestSuccess() {
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        assertDoesNotThrow(() -> itemService.deleteItem(1L, 1L));
    }

    @Test
    public void deleteItemNotFoundTestFail() {
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.deleteItem(1L, 1L));
    }

    @Test
    public void deleteItemUserNotOwnerTestFail() {
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        assertThrows(IllegalEntityAccessException.class, () -> itemService.deleteItem(1L, 2L));
    }

    @Test
    public void getItemByIdTestSuccess() {
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        assertEquals(1L, itemService.getItemById(1L).getId());
    }

    @Test
    public void getItemByIdNotFoundTestFail() {
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getItemById(1L));
    }

    @Test
    public void getItemDtoByIdTestSuccess() {
        when(commentRepository.findAllByItemId(Mockito.anyLong())).thenReturn(List.of(comment));
        when(bookingRepository.findByItemIdAndStartAfterAndStatus(Mockito.anyLong(), any(), any())).thenReturn(booking);
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        ItemDtoWithBooking itemDto = itemService.getItemDtoById(1L, 1L);

        assertEquals(1L, itemDto.getId());
        assertEquals("Название", itemDto.getName());
        assertEquals("Описание", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(1L, itemDto.getOwner());
        assertEquals(1L, itemDto.getRequestId());
        assertNull(itemDto.getLastBooking());
        assertEquals(BookingMapper.toBookingDto(booking), itemDto.getNextBooking());
        assertEquals(1, itemDto.getComments().size());
    }

    @Test
    public void getItemDtoByIdNotFoundTestFail() {
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getItemDtoById(1L, 1L));
    }

    @Test
    public void getAllItemsByUserIdTestSuccess() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(user);
        when(itemRepository.findAllByOwner(Mockito.anyLong(), any())).thenReturn(List.of(item));

        List<ItemDtoWithBooking> items = itemService.getAllItemsByUserId(1L, 0, 10);

        assertEquals(1, items.size());
    }

    @Test
    public void getAllItemsByUserIdNotFoundTestFail() {
        when(userService.getUserById(Mockito.anyLong())).thenThrow(notFoundException);

        assertThrows(EntityNotFoundException.class, () -> itemService.getAllItemsByUserId(999L, 0, 10));
    }

    @Test
    public void getItemsByTextTestSuccess() {
        when(itemRepository.findByText(Mockito.anyString(), any())).thenReturn(List.of(item));

        assertEquals(1, itemService.getItemsByText("Название", 0, 10).size());
        assertEquals(0, itemService.getItemsByText("", 0, 10).size());
    }

    @Test
    public void addCommentTestSuccess() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(user);
        when(bookingRepository.findByBookerIdAndItemIdAndEndIsBefore(Mockito.anyLong(), Mockito.anyLong(), any())).thenReturn(booking);
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto commentDto = itemService.addComment(CommentMapper.toCommentDto(comment), 1L, 1L);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        assertEquals(comment.getItemId(), commentDto.getItemId());
    }

    @Test
    public void addCommentUserNotFoundTestFail() {
        when(userService.getUserById(Mockito.anyLong())).thenThrow(notFoundException);

        assertThrows(EntityNotFoundException.class, () -> itemService.addComment(CommentMapper.toCommentDto(comment), 1L, 999L));
    }

    @Test
    public void addCommentIllegalAccessTestFail() {
        when(bookingRepository.findByBookerIdAndItemIdAndEndIsBefore(Mockito.anyLong(), Mockito.anyLong(), any())).thenReturn(null);

        assertThrows(EntityAvailabilityException.class, () -> itemService.addComment(CommentMapper.toCommentDto(comment), 1L, 1L));
    }

    @Test
    public void getAllItemsByRequestIdTestSuccess() {
        when(itemRepository.findAllByRequestId(Mockito.anyLong())).thenReturn(List.of(item));

        assertEquals(1, itemService.getAllItemsByRequestId(1L).size());
    }
}
