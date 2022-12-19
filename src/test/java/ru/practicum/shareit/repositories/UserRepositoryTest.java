package ru.practicum.shareit.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dao.impl.UserRepositoryImpl;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserRepositoryTest {
    private UserRepository userRepository;
    private User firstUser;
    private User secondUser;
    private User thirdUser;

    @BeforeEach
    public void beforeEach() {
        userRepository = new UserRepositoryImpl();
        firstUser = new User(1, "Первый", "first@yandex.ru");
        secondUser = new User(2, "Второй", "second@yandex.ru");
        thirdUser = new User(3, "Третий", "third@yandex.ru");
    }

    @Test
    public void addUser() {
        assertEquals(0, userRepository.getAllUsers().size());
        userRepository.addUser(firstUser);
        assertEquals(1, userRepository.getAllUsers().size());
        assertThrows(EntityAlreadyExistsException.class, () -> userRepository.addUser(firstUser));
    }

    @Test
    public void updateUser() {
        userRepository.addUser(firstUser);
        userRepository.updateUser(1, secondUser);
        User updatedUser = userRepository.getUserById(1);
        assertEquals(secondUser.getName(), updatedUser.getName());
        assertEquals(secondUser.getEmail(), updatedUser.getEmail());
        userRepository.addUser(thirdUser);
        assertThrows(EntityAlreadyExistsException.class, () -> userRepository.updateUser(2, secondUser));
    }

    @Test
    public void deleteUser() {
        userRepository.addUser(firstUser);
        userRepository.addUser(secondUser);
        assertEquals(2, userRepository.getAllUsers().size());
        userRepository.deleteUser(1);
        assertEquals(1, userRepository.getAllUsers().size());
        assertThrows(EntityNotFoundException.class, () -> userRepository.deleteUser(999));
    }

    @Test
    public void getUserById() {
        userRepository.addUser(firstUser);
        userRepository.addUser(secondUser);
        assertEquals(firstUser, userRepository.getUserById(1));
        assertEquals(secondUser, userRepository.getUserById(2));
        assertThrows(EntityNotFoundException.class, () -> userRepository.getUserById(999));
    }

    @Test
    public void getAllUsers() {
        userRepository.addUser(firstUser);
        userRepository.addUser(secondUser);
        userRepository.addUser(thirdUser);
        assertEquals(3, userRepository.getAllUsers().size());
    }
}
