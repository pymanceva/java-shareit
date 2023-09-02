package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@UtilityClass
public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        if (item.getRequestId() != null) {
            itemDto.setRequestId(item.getRequestId());
        }

        return itemDto;
    }

    public static List<ItemDto> mapToItemDto(Iterable<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(mapToItemDto(item));
        }
        return dtos;
    }

    public static ItemDtoWithBookings mapToItemDtoForOwner(Item item,
                                                           Booking lastBooking,
                                                           Booking nextBooking,
                                                           Collection<Comment> comments) {
        ItemDtoWithBookings itemDtoWithBookings = new ItemDtoWithBookings();
        itemDtoWithBookings.setId(item.getId());
        itemDtoWithBookings.setName(item.getName());
        itemDtoWithBookings.setDescription(item.getDescription());
        itemDtoWithBookings.setAvailable(item.getAvailable());
        if (lastBooking != null) {
            itemDtoWithBookings.setLastBooking(BookingMapper.mapToBookingDtoForOwner(lastBooking));
        }
        if (nextBooking != null) {
            itemDtoWithBookings.setNextBooking(BookingMapper.mapToBookingDtoForOwner(nextBooking));
        }
        if (item.getRequestId() != null) {
            itemDtoWithBookings.setRequestId(item.getRequestId());
        }
        itemDtoWithBookings.setComments(CommentMapper.mapToCommentDto(comments));

        return itemDtoWithBookings;
    }

    public static Item mapToItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setRequestId(itemDto.getRequestId());

        return item;
    }
}
