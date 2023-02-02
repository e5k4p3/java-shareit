package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;
import ru.practicum.shareit.validation.ValidationErrorsHandler;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addUser(@Validated({Create.class}) @RequestBody UserDto userDto,
                                          BindingResult bindingResult) {
        ValidationErrorsHandler.logValidationErrors(bindingResult);
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                              @Validated({Update.class}) @RequestBody UserDto userDto,
                              BindingResult bindingResult) {
        ValidationErrorsHandler.logValidationErrors(bindingResult);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        return userClient.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        return userClient.getUserById(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }
}