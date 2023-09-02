package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingOutgoingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingOutgoingDtoTest {
    private final UserDto userDto = new UserDto(
            1L,
            "User1",
            "user1@yandex.ru");
    private final ItemDto itemDto = new ItemDto(
            1L,
            "Item1",
            "DescriptionItem1",
            true,
            1L);
    private final BookingOutgoingDto bookingOutgoingDto = new BookingOutgoingDto(
            2L,
            LocalDateTime.of(2023, 1, 1, 0, 0, 0),
            LocalDateTime.of(2023, 1, 1, 1, 1, 1),
            itemDto,
            userDto,
            BookingStatus.WAITING);
    @Autowired
    private JacksonTester<BookingOutgoingDto> json;

    @Test
    void testBookingDto() throws Exception {
        JsonContent<BookingOutgoingDto> result = json.write(bookingOutgoingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(bookingOutgoingDto.getId()));
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingOutgoingDto.getStart().toString() + ":00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingOutgoingDto.getEnd().toString());
        assertThat(result).extractingJsonPathValue("$.item").extracting("id").isEqualTo(Math.toIntExact(itemDto.getId()));
        assertThat(result).extractingJsonPathValue("$.booker").extracting("id").isEqualTo(Math.toIntExact(userDto.getId()));
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(BookingStatus.WAITING.toString());
    }
}