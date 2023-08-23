package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {
    private final CommentDto commentDto = new CommentDto(
            1L,
            "Text",
            "Name",
            LocalDateTime.of(2023, 1, 1, 0, 0, 0)
    );

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testCommentDto() throws Exception {
        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(commentDto.getId()));
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Text");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Name");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(commentDto.getCreated().toString() + ":00");
    }
}