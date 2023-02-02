package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.util.UserMapper;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    @Test
    public void toUserDtoTest() {
        User user = new User(1L, "Имя", "email@email.com");

        UserDto userDto = UserMapper.toUserDto(user);

        assertEquals(1, userDto.getId());
        assertEquals("Имя", userDto.getName());
        assertEquals("email@email.com", userDto.getEmail());
    }

    @Test
    public void toUserTest() {
        UserDto userDto = new UserDto(1L, "Имя", "email@email.com");

        User user = UserMapper.toUser(userDto);

        assertEquals(1, user.getId());
        assertEquals("Имя", user.getName());
        assertEquals("email@email.com", user.getEmail());
    }
}
