package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingIncomingDtoTest {
    private final UserDto userDto = new UserDto(
            1L,
            "User1",
            "user1@yandex.ru");
    private final BookingIncomingDto bookingIncomingDto = new BookingIncomingDto(
            2L,
            LocalDateTime.of(2023, 1, 1, 0, 0, 0),
            LocalDateTime.of(2023, 1, 1, 1, 1, 1),
            1L,
            userDto,
            BookingStatus.WAITING);
    @Autowired
    private JacksonTester<BookingIncomingDto> json;

    @Test
    void testBookingDto() throws Exception {
        JsonContent<BookingIncomingDto> result = json.write(bookingIncomingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(bookingIncomingDto.getId()));
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingIncomingDto.getStart().toString() + ":00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingIncomingDto.getEnd().toString());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(Math.toIntExact(bookingIncomingDto.getItemId()));
        assertThat(result).extractingJsonPathValue("$.booker").extracting("id").isEqualTo(Math.toIntExact(userDto.getId()));
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(BookingStatus.WAITING.toString());
    }
}