package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым.", groups = Create.class)
    private String name;
    @Email(message = "Email должен быть корректным.", groups = {Create.class, Update.class})
    @NotNull(message = "Нельзя создать пользователя без email.", groups = Create.class)
    private String email;
}
