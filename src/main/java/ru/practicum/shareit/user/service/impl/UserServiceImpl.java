package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User addUser(User user) {
        return userRepository.addUser(user);
    }

    @Override
    public User updateUser(int userId, User user) {
        return userRepository.updateUser(userId, user);
    }

    @Override
    public void deleteUser(int userId) {
        userRepository.deleteUser(userId);
    }

    @Override
    public User getUserById(int userId) {
        return userRepository.getUserById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }
}
