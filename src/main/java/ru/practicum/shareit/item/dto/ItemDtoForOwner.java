package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.BookingDtoForOwner;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@ToString
public class ItemDtoForOwner extends ItemDto {

    private BookingDtoForOwner lastBooking;

    private BookingDtoForOwner nextBooking;

    private Collection<CommentDto> comments = new ArrayList<>();
}
