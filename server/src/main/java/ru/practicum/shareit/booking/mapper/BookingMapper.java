package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDtoForOwner;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingOutgoingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BookingMapper {
    public static BookingOutgoingDto mapToBookingOutgoingDto(Booking booking) {
        BookingOutgoingDto bookingOutGoingDto = new BookingOutgoingDto();
        bookingOutGoingDto.setId(booking.getId());
        bookingOutGoingDto.setStart(booking.getStart());
        bookingOutGoingDto.setEnd(booking.getEnd());
        bookingOutGoingDto.setItem(ItemMapper.mapToItemDto(booking.getItem()));
        bookingOutGoingDto.setBooker(UserMapper.mapToUserDto(booking.getBooker()));
        bookingOutGoingDto.setStatus(booking.getStatus());

        return bookingOutGoingDto;
    }

    public static List<BookingOutgoingDto> mapToBookingOutgoingDto(Iterable<Booking> bookings) {
        List<BookingOutgoingDto> result = new ArrayList<>();

        for (Booking booking : bookings) {
            result.add(mapToBookingOutgoingDto(booking));
        }

        return result;
    }

    public static BookingDtoForOwner mapToBookingDtoForOwner(Booking booking) {
        BookingDtoForOwner bookingDtoForOwner = new BookingDtoForOwner();
        bookingDtoForOwner.setId(booking.getId());
        bookingDtoForOwner.setBookerId(booking.getBooker().getId());

        return bookingDtoForOwner;
    }

    public static Booking mapToBooking(BookingIncomingDto bookingIncomingDto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setId(bookingIncomingDto.getId());
        booking.setStart(bookingIncomingDto.getStart());
        booking.setEnd(bookingIncomingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(bookingIncomingDto.getStatus());

        return booking;
    }
}
