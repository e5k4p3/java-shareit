package ru.practicum.shareit.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank(message = "Текст комментария не может быть пустым.")
    private String text;
    private String authorName;
    private Long itemId;
    private LocalDateTime created;
}
