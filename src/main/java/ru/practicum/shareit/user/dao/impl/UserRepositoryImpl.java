package ru.practicum.shareit.user.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.*;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> allUsers = new HashMap<>();
    private int id = 1;

    @Override
    public User addUser(User user) {
        checkUserEmailExistence(user.getEmail());
        user.setId(getNewId());
        allUsers.put(user.getId(), user);
        log.info("Пользователь с email " + user.getEmail() + " добавлен.");
        return user;
    }

    @Override
    public User updateUser(int userId, User user) {
        checkUserIdExistence(userId);
        if (user.getName() != null && !user.getName().isBlank()) {
            allUsers.get(userId).setName(user.getName());
        }
        if (user.getEmail() != null) {
            checkUserEmailExistence(user.getEmail());
            allUsers.get(userId).setEmail(user.getEmail());
        }
        log.info("Пользователь с id " + userId + " обновлен.");
        return getUserById(userId);
    }

    @Override
    public void deleteUser(int userId) {
        checkUserIdExistence(userId);
        allUsers.remove(userId);
        log.info("Пользователь с id " + userId + " удален.");
    }

    @Override
    public User getUserById(int userId) {
        checkUserIdExistence(userId);
        return allUsers.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(allUsers.values());
    }

    private void checkUserIdExistence(int userId) {
        if (!allUsers.containsKey(userId)) {
            throw new EntityNotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }

    private void checkUserEmailExistence(String email) {
        Optional<User> user = allUsers.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
        if (user.isPresent()) {
            throw new EntityAlreadyExistsException("Пользователь с таким email уже существует.");
        }
    }

    private int getNewId() {
        return id++;
    }
}
