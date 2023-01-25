package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    JacksonTester<CommentDto> jacksonTester;

    @Test
    public void commentDtoJsonTest() throws IOException {
        CommentDto commentDto = new CommentDto(
                1L,
                "Текст",
                "Автор",
                1L,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1)
        );

        JsonContent<CommentDto> commentDtoResult = jacksonTester.write(commentDto);

        assertThat(commentDtoResult).extractingJsonPathNumberValue("$.id").isEqualTo(commentDto.getId().intValue());
        assertThat(commentDtoResult).extractingJsonPathStringValue("$.text").isEqualTo(commentDto.getText());
        assertThat(commentDtoResult).extractingJsonPathStringValue("$.authorName").isEqualTo(commentDto.getAuthorName());
        assertThat(commentDtoResult).extractingJsonPathNumberValue("$.itemId").isEqualTo(commentDto.getItemId().intValue());
        assertThat(commentDtoResult).extractingJsonPathValue("$.created").isEqualTo(commentDto.getCreated().toString());
    }
}
