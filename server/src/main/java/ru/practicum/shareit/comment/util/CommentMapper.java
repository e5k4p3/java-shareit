package ru.practicum.shareit.comment.util;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getItemId(),
                comment.getCreated()
        );
    }

    public static Comment toComment(CommentDto commentDto, User author, Long itemId) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                author,
                itemId,
                commentDto.getCreated()
        );
    }
}
