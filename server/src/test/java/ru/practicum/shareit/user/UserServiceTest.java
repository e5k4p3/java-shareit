package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    private User firstUser;
    private User secondUser;

    @BeforeEach
    public void beforeEach() {
        firstUser = new User(1L, "Имя первого", "first@email.com");
        secondUser = new User(2L, "Имя второго", "second@email.com");
    }

    @Test
    public void addUserTestSuccess() {
        when(userRepository.save(any())).thenReturn(firstUser);

        User user = userService.addUser(firstUser);

        assertEquals(firstUser.getId(), user.getId());
        assertEquals(firstUser.getName(), user.getName());
        assertEquals(firstUser.getEmail(), user.getEmail());
    }

    @Test
    public void addUserDuplicateEmailTestFail() {
        when(userRepository.save(any())).thenThrow(new EntityAlreadyExistsException("Уже существует."));

        assertThrows(EntityAlreadyExistsException.class, () -> userService.addUser(firstUser));
    }

    @Test
    public void updateUserTestSuccess() {
        when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(firstUser));

        secondUser.setId(1L);

        User updatedUser = userService.updateUser(1L, secondUser);

        assertEquals(secondUser.getId(), updatedUser.getId());
        assertEquals(secondUser.getName(), updatedUser.getName());
        assertEquals(secondUser.getEmail(), updatedUser.getEmail());
    }

    @Test
    public void updateUserDuplicateEmailTestFail() {
        when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> userService.updateUser(1L, firstUser));
    }

    @Test
    public void updateUserNotFoundTestFail() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(1L, firstUser));
    }

    @Test
    public void deleteUserTestSuccess() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(firstUser));

        assertDoesNotThrow(() -> userService.deleteUser(1L));
    }

    @Test
    public void deleteUserNotFoundTestFail() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    public void getUserByIdTestSuccess() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(firstUser));

        User user = userService.getUserById(1L);

        assertEquals(1L, user.getId());
        assertEquals("Имя первого", user.getName());
        assertEquals("first@email.com", user.getEmail());
    }

    @Test
    public void getUserByIdNotFoundTestFail() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    public void getAllUsersTestSuccess() {
        when(userRepository.findAll()).thenReturn(List.of(firstUser, secondUser));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
    }
}
