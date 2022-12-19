package ru.practicum.shareit.controllers;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private Gson gson;
    private UserDto correctUser;
    private UserDto userWithIncorrectName;
    private UserDto userWithIncorrectEmail;
    private UserDto userWithDuplicateEmail;

    @BeforeEach
    public void beforeEach() {
        gson = new Gson();
        correctUser = new UserDto(1, "e5k4p3", "e5k4p3@gmail.com");
        userWithIncorrectName = new UserDto(2, "", "e5k4p3@gmail.com");
        userWithIncorrectEmail = new UserDto(3, "e5k4p3", "@gmail.com");
        userWithDuplicateEmail = new UserDto(4, "e5k4p3", "e5k4p3@gmail.com");
    }

    @Test
    public void validationTest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(correctUser)))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(userWithIncorrectName)))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(userWithIncorrectEmail)))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(userWithDuplicateEmail)))
                .andExpect(status().is4xxClientError());
    }
}
