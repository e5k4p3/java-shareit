package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.impl.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemRequestServiceTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    private User firstUser;
    private User secondUser;
    private Item item;
    private ItemRequest itemRequest;
    private final EntityNotFoundException notFoundException = new EntityNotFoundException("Не найдено.");

    @BeforeEach
    public void beforeEach() {
        firstUser = new User(1L, "Имя первого", "first@email.com");
        secondUser = new User(2L, "Имя второго", "second@email.com");
        item = new Item(1L, "Название", "Описание", true, 2L, 1L);
        itemRequest = new ItemRequest(
                1L,
                "Описание",
                firstUser,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1
                )
        );
    }

    @Test
    public void addRequestTestSuccess() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(firstUser);
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDtoResponse itemRequestDto = itemRequestService.addRequest(itemRequest, 1L);

        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());
    }

    @Test
    public void addRequestUserNotFoundTestFail() {
        when(userService.getUserById(Mockito.anyLong())).thenThrow(notFoundException);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.addRequest(itemRequest, 999L));
    }

    @Test
    public void getRequestByIdTestSuccess() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(firstUser);
        when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemService.getAllItemsByRequestId(Mockito.anyLong())).thenReturn(List.of(item));

        ItemRequestDtoWithItems itemRequestDto = itemRequestService.getRequestById(1L, 1L);

        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());
        assertEquals(1, itemRequestDto.getItems().size());
    }

    @Test
    public void getRequestByIdNotFoundTestFail() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(firstUser);
        when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getRequestById(1L, 1L));
    }

    @Test
    public void getRequestByIdUserNotFoundTestFail() {
        when(userService.getUserById(Mockito.anyLong())).thenThrow(notFoundException);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getRequestById(1L, 999L));
    }

    @Test
    public void getAllRequestByUserIdTestSuccess() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(firstUser);
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(Mockito.anyLong())).thenReturn(List.of(itemRequest));
        when(itemService.getAllItemsByRequestId(Mockito.anyLong())).thenReturn(List.of(item));

        assertEquals(1, itemRequestService.getAllRequestsByUserId(1L).size());
    }

    @Test
    public void getAllRequestsByUserIdNotFound() {
        when(userService.getUserById(Mockito.anyLong())).thenThrow(notFoundException);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getAllRequestsByUserId(999L));
    }

    @Test
    public void getAllRequestsTestSuccess() {
        ItemRequest newItemRequest = new ItemRequest(
                2L,
                "Описание",
                secondUser,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1)
        );
        Item newItem = new Item(2L, "Название", "Описание", true, 1L, 2L);
        when(userService.getUserById(Mockito.anyLong())).thenReturn(secondUser);
        when(itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(Mockito.anyLong(), any())).thenReturn(List.of(newItemRequest));
        when(itemService.getAllItemsByRequestId(Mockito.anyLong())).thenReturn(List.of(newItem));

        assertEquals(1, itemRequestService.getAllRequests(2L, 0, 10).size());
    }

    @Test
    public void getAllRequestsUserNotFoundTestFail() {
        when(userService.getUserById(Mockito.anyLong())).thenThrow(notFoundException);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getAllRequests(999L, 0, 10));
    }
}
