package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exceptions.EntityAvailabilityException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.IllegalEntityAccessException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.util.ItemMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    private Item correctItem;
    private Item itemWithoutHeader;
    private Item itemWithoutExistingUser;
    private Item itemWithIncorrectName;
    private Item itemWithIncorrectDescription;
    private Item itemWithoutAvailableField;
    private CommentDto commentDto;
    private final ValidationException validationException = new ValidationException("Валидация.");
    private final EntityNotFoundException notFoundException = new EntityNotFoundException("Не найдено.");
    private final IllegalEntityAccessException illegalEntityAccessException = new IllegalEntityAccessException("Нет доступа.");

    public ItemControllerTest() {
    }

    @BeforeEach
    public void beforeEach() {
        correctItem = new Item(1L, "Корректный предмет", "Корректное описание", true, 1L, 1L);
        itemWithoutHeader = new Item(2L, "Без заголовка", "Без заголовка", true, 1L, 1L);
        itemWithoutExistingUser = new Item(3L, "Без юзера", "Без юзера", true, 999L, 1L);
        itemWithIncorrectName = new Item(4L, "", "Без названия", true, 1L, 1L);
        itemWithIncorrectDescription = new Item(5L, "Без описания", "", true, 1L, 1L);
        itemWithoutAvailableField = new Item(6L, "Без поля", "Без поля", null, 1L, 1L);
        commentDto = new CommentDto(1L, "Комментарий", "Имя", 1L, LocalDateTime.now());
    }

    @Test
    public void validationTestSuccess() throws Exception {
        when(itemService.addItem(Mockito.anyLong(), any())).thenReturn(correctItem);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctItem))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(correctItem.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(correctItem.getName())))
                .andExpect(jsonPath("$.description", is(correctItem.getDescription())))
                .andExpect(jsonPath("$.available", is(correctItem.getAvailable())))
                .andExpect(jsonPath("$.owner", is(correctItem.getOwner()), Long.class))
                .andExpect(jsonPath("$.requestId", is(correctItem.getRequestId()), Long.class));
    }

    @Test
    public void validationTestFail() throws Exception {
        when(itemService.addItem(Mockito.anyLong(), any())).thenThrow(validationException);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemWithIncorrectName))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Ошибка валидации.")));

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemWithIncorrectDescription))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Ошибка валидации.")));

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemWithoutAvailableField))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Ошибка валидации.")));
    }

    @Test
    public void addItemNoHeaderFail() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemWithoutHeader)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void addItemUserNotFoundFail() throws Exception {
        when(itemService.addItem(Mockito.anyLong(), any())).thenThrow(notFoundException);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemWithoutExistingUser))
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Объект не найден.")));
    }

    @Test
    public void updateTestSuccess() throws Exception {
        when(itemService.updateItem(Mockito.anyLong(), Mockito.anyLong(), any())).thenReturn(correctItem);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctItem))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(correctItem.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(correctItem.getName())))
                .andExpect(jsonPath("$.description", is(correctItem.getDescription())))
                .andExpect(jsonPath("$.available", is(correctItem.getAvailable())))
                .andExpect(jsonPath("$.owner", is(correctItem.getOwner()), Long.class))
                .andExpect(jsonPath("$.requestId", is(correctItem.getRequestId()), Long.class));
    }

    @Test
    public void updateTestValidationFail() throws Exception {
        when(itemService.updateItem(Mockito.anyLong(), Mockito.anyLong(), any())).thenThrow(validationException);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemWithIncorrectName))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Ошибка валидации.")));
    }

    @Test
    public void updateTestNotFoundFail() throws Exception {
        when(itemService.updateItem(Mockito.anyLong(), Mockito.anyLong(), any())).thenThrow(notFoundException);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctItem))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Объект не найден.")));
    }

    @Test
    public void updateTestIllegalAccessFail() throws Exception {
        when(itemService.updateItem(Mockito.anyLong(), Mockito.anyLong(), any())).thenThrow(illegalEntityAccessException);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctItem))
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Отсутствие прав доступа к объекту.")));
    }

    @Test
    public void deleteTestSuccess() throws Exception {
        Mockito.doNothing().when(itemService).deleteItem(Mockito.anyLong(), Mockito.anyLong());

        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void deleteTestNotFoundFail() throws Exception {
        Mockito.doThrow(notFoundException).when(itemService).deleteItem(Mockito.anyLong(), Mockito.anyLong());

        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Объект не найден.")));
    }

    @Test
    public void deleteTestIllegalAccessFail() throws Exception {
        Mockito.doThrow(illegalEntityAccessException).when(itemService).deleteItem(Mockito.anyLong(), Mockito.anyLong());

        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Отсутствие прав доступа к объекту.")));
    }

    @Test
    public void getByIdSuccess() throws Exception {
        when(itemService.getItemDtoById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(ItemMapper.toItemDtoWithBooking(correctItem));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(correctItem.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(correctItem.getName())))
                .andExpect(jsonPath("$.description", is(correctItem.getDescription())))
                .andExpect(jsonPath("$.available", is(correctItem.getAvailable())))
                .andExpect(jsonPath("$.owner", is(correctItem.getOwner()), Long.class))
                .andExpect(jsonPath("$.requestId", is(correctItem.getRequestId()), Long.class));
    }

    @Test
    public void getByIdNotFoundFail() throws Exception {
        when(itemService.getItemDtoById(Mockito.anyLong(), Mockito.anyLong())).thenThrow(notFoundException);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Объект не найден.")));
    }

    @Test
    public void getAllByUserIdSuccess() throws Exception {
        when(itemService.getAllItemsByUserId(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(ItemMapper.toItemDtoWithBooking(correctItem)));

        mockMvc.perform(get("/items?from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void getAllByUserIdNotFoundFail() throws Exception {
        when(itemService.getAllItemsByUserId(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenThrow(notFoundException);

        mockMvc.perform(get("/items?from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Объект не найден.")));
    }

    @Test
    public void getAllByTextSuccess() throws Exception {
        when(itemService.getItemsByText(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(correctItem));

        mockMvc.perform(get("/items/search?text=предмет&from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void addCommentSuccess() throws Exception {
        when(itemService.addComment(any(), Mockito.anyLong(), Mockito.anyLong())).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.itemId", is(commentDto.getItemId()), Long.class));
    }

    @Test
    public void addCommentValidationFail() throws Exception {
        when(itemService.addComment(any(), Mockito.anyLong(), Mockito.anyLong())).thenThrow(validationException);

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Ошибка валидации.")));
    }

    @Test
    public void addCommentAvailabilityFail() throws Exception {
        when(itemService.addComment(any(), Mockito.anyLong(), Mockito.anyLong())).thenThrow(new EntityAvailabilityException("Недоступен."));

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Недоступность объекта.")));
    }
}