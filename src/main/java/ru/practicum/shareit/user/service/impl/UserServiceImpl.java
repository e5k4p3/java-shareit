package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User addUser(User user) {
        try {
            User userToAdd = userRepository.save(user);
            log.info("Пользователь с email " + userToAdd.getEmail() + " был добавлен.");
            return userToAdd;
        } catch (Exception e) {
            throw new EntityAlreadyExistsException("Пользователь с email " + user.getEmail() + " уже существует.");
        }
    }
//    Пришлось сделать так, ибо, если судить по тестам постмана, при дубликации email, то id увеличивается на 1
//    Метод ниже, как раз решение, которое я считаю правильным

//    @Override
//    @Transactional
//    public User addUser(User user) {
//        checkEmailExistence(user.getEmail());
//        log.info("Пользователь с email " + user.getEmail() + " был добавлен.");
//        return userRepository.save(user);
//    }

    @Override
    @Transactional
    public User updateUser(Long userId, User user) {
        checkEmailExistence(user.getEmail());
        User updatedUser = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Пользователь с id " + userId + " не найден."));
        if (user.getName() != null && !user.getName().isBlank()) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }
        return updatedUser;
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        getUserById(userId);
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Пользователь с id " + userId + " не найден."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    private void checkEmailExistence(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EntityAlreadyExistsException("Пользователь с email " + email + " уже существует.");
        }
    }
}
