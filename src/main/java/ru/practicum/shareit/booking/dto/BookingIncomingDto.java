package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingIncomingDto {
    private Long id;

    @NotNull
    @Future
    private LocalDateTime start;

    @NotNull
    @Future
    private LocalDateTime end;

    private Long itemId;

    private UserDto booker;

    private BookingStatus status;

    public boolean validate() {
        return end.isAfter(start);
    }
}
