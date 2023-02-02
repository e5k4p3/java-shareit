package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ItemRequestDtoResponse {
    private Long id;
    private String description;
    private LocalDateTime created;
}
