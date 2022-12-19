package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {
    private int id;
    @NotBlank(message = "Название предмета не может быть пустым.")
    private String name;
    @NotBlank(message = "Описание предмета не может быть пустым.")
    private String description;
    @NotNull(message = "Нужно указать доступность предмета.")
    private Boolean available;
    private int owner;
}
