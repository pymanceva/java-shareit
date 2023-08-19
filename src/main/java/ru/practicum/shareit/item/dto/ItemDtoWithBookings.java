package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoForOwner;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemDtoWithBookings {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingDtoForOwner lastBooking;
    private BookingDtoForOwner nextBooking;
    private Collection<CommentDto> comments = new ArrayList<>();
}
