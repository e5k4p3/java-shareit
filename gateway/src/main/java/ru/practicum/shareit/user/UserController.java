package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;
import ru.practicum.shareit.validation.ValidationErrorsHandler;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addUser(@Validated({Create.class}) @RequestBody UserDto userDto,
                                          BindingResult bindingResult) {
        log.debug("Получен POST запрос на добаление пользователя.");
        ValidationErrorsHandler.logValidationErrors(bindingResult);
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                              @Validated({Update.class}) @RequestBody UserDto userDto,
                              BindingResult bindingResult) {
        log.debug("Получен PATCH запрос на изменение пользователя.");
        ValidationErrorsHandler.logValidationErrors(bindingResult);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.debug("Получен DELETE запрос на удаление пользователя.");
        return userClient.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.debug("Получен GET запрос на получение пользователя по id.");
        return userClient.getUserById(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllUsers() {
        log.debug("Получен GET запрос на получение всех пользователей.");
        return userClient.getAllUsers();
    }
}