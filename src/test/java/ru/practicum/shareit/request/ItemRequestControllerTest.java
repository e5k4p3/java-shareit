package ru.practicum.shareit.request;

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
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;
    private ItemRequestDto correctRequestDto;
    private ItemRequestDto requestDtoWithInvalidDescription;
    private ItemRequestDtoResponse requestDtoResponse;
    private ItemRequestDtoWithItems requestDtoWithItems;
    private final ValidationException validationException = new ValidationException("Валидация.");
    private final EntityNotFoundException notFoundException = new EntityNotFoundException("Не найдено.");

    @BeforeEach
    public void beforeEach() {
        correctRequestDto = new ItemRequestDto(1L, "Описание");
        requestDtoWithInvalidDescription = new ItemRequestDto(1L, "");
        requestDtoResponse = new ItemRequestDtoResponse(
                1L,
                "Описание",
                LocalDateTime.of(2024, 1, 1, 1, 1, 1));
        requestDtoWithItems = new ItemRequestDtoWithItems(
                1L,
                "Описание",
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                new ArrayList<>()
        );
    }

    @Test
    public void validationTestSuccess() throws Exception {
        when(itemRequestService.addRequest(any(), Mockito.anyLong())).thenReturn(requestDtoResponse);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctRequestDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(requestDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoResponse.getDescription())))
                .andExpect(jsonPath("$.created", is(requestDtoResponse.getCreated().toString())));
    }

    @Test
    public void validationTestFail() throws Exception {
        when(itemRequestService.addRequest(any(), Mockito.anyLong())).thenThrow(validationException);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDtoWithInvalidDescription))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void addItemRequestWithoutHeaderTestFail() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctRequestDto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void getAllRequestsByUserIdTestSuccess() throws Exception {
        when(itemRequestService.getAllRequestsByUserId(Mockito.anyLong())).thenReturn(List.of(requestDtoWithItems));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void getAllRequestsByUserIdNotFoundTestFail() throws Exception {
        when(itemRequestService.getAllRequestsByUserId(Mockito.anyLong())).thenThrow(notFoundException);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Объект не найден.")));
    }

    @Test
    public void getAllRequestsByUserIdTestWithoutHeaderTestFail() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void getAllRequestsTestSuccess() throws Exception {
        when(itemRequestService.getAllRequests(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(requestDtoWithItems));

        mockMvc.perform(get("/requests/all?from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void getAllRequestsNotFoundTestFail() throws Exception {
        when(itemRequestService.getAllRequests(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenThrow(notFoundException);

        mockMvc.perform(get("/requests/all?from=0&size=10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Объект не найден.")));
    }

    @Test
    public void getAllRequestsWithoutHeaderTestFail() throws Exception {
        mockMvc.perform(get("/requests/all?from=0&size=10"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void getRequestByIdTestSuccess() throws Exception {
        when(itemRequestService.getRequestById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(requestDtoWithItems);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(requestDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoResponse.getDescription())))
                .andExpect(jsonPath("$.created", is(requestDtoResponse.getCreated().toString())))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    public void getRequestByIdNotFoundTestFail() throws Exception {
        when(itemRequestService.getRequestById(Mockito.anyLong(), Mockito.anyLong())).thenThrow(notFoundException);

        mockMvc.perform(get("/requests/999")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Объект не найден.")));

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Объект не найден.")));
    }

    @Test
    public void getRequestByIdWithoutHeaderTestFail() throws Exception {
        mockMvc.perform(get("/requests/1"))
                .andExpect(status().is4xxClientError());
    }
}
