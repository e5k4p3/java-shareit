package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.util.CommentMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest {
    private final User user = new User(1L, "Имя", "email@email.com");

    @Test
    public void toCommentDtoTest() {
        Comment comment = new Comment(
                1L,
                "Текст",
                user,
                1L,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1)
        );

        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        assertEquals(1L, commentDto.getId());
        assertEquals("Текст", commentDto.getText());
        assertEquals("Имя", commentDto.getAuthorName());
        assertEquals(1L, commentDto.getItemId());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }

    @Test
    public void toCommentTest() {
        CommentDto commentDto = new CommentDto(
                1L,
                "Текст",
                "Имя",
                1L,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1)
        );

        Comment comment = CommentMapper.toComment(commentDto, user, 1L);

        assertEquals(1L, comment.getId());
        assertEquals("Текст", comment.getText());
        assertEquals("Имя", comment.getAuthor().getName());
        assertEquals(1L, comment.getItemId());
        assertEquals(commentDto.getCreated(), comment.getCreated());
    }
}
