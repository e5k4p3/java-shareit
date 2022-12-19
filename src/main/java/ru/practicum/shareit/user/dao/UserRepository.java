package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User addUser(User user);

    User updateUser(int userId, User user);

    void deleteUser(int userId);

    User getUserById(int userId);

    List<User> getAllUsers();
}
