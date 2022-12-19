package ru.practicum.shareit.controllers;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private Gson gson;
    private UserDto user;
    private ItemDto correctItem;
    private ItemDto itemWithoutHeader;
    private ItemDto itemWithoutExistingUser;
    private ItemDto itemWithIncorrectName;
    private ItemDto itemWithIncorrectDescription;
    private ItemDto itemWithoutAvailableField;

    @BeforeEach
    public void beforeEach() {
        gson = new Gson();
        user = new UserDto(1, "Имя", "mail@mail.ru");
        correctItem = new ItemDto(1, "Корректный предмет", "Корректное описание", true, 1);
        itemWithoutHeader = new ItemDto(2, "Без заголовка", "Без заголовка", true, 1);
        itemWithoutExistingUser = new ItemDto(3, "Без юзера", "Без юзера", true, 999);
        itemWithIncorrectName = new ItemDto(4, "", "Без названия", true, 1);
        itemWithIncorrectDescription = new ItemDto(5, "Без описания", "", true, 1);
        itemWithoutAvailableField = new ItemDto(6, "Без поля", "Без поля", null, 1);
    }

    @Test
    public void validationTest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(user)))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(correctItem))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(itemWithoutHeader)))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(itemWithoutExistingUser))
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(itemWithIncorrectName))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(itemWithIncorrectDescription))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(itemWithoutAvailableField))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }
}
