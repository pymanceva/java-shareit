package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.RequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RequestDtoTest {
    private final RequestDto requestDto = new RequestDto(
            1L,
            "Description",
            1L,
            LocalDateTime.of(2023, 1, 1, 0, 0, 0),
            new ArrayList<>());

    @Autowired
    private JacksonTester<RequestDto> json;

    @Test
    void testRequestDto() throws Exception {
        JsonContent<RequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(requestDto.getId()));
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(requestDto.getCreated().toString() + ":00");
        assertThat(result).extractingJsonPathNumberValue("$.requesterId").isEqualTo(Math.toIntExact(requestDto.getRequesterId()));
        assertThat(result).extractingJsonPathArrayValue("$.items").isEqualTo(requestDto.getItems());
    }
}