package ru.practicum.shareit.comment.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    private Long itemId;
    private LocalDateTime created;
}
