package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Описание запроса не может быть пустым.")
    private String description;
}
