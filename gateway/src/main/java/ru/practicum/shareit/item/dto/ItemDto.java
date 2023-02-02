package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Название предмета не может быть пустым.")
    private String name;
    @NotBlank(message = "Описание предмета не может быть пустым.")
    private String description;
    @NotNull(message = "Нужно указать доступность предмета.")
    private Boolean available;
    private Long owner;
    private Long requestId;
}