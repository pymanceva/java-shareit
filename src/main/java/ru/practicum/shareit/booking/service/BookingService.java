package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingOutgoingDto;
import ru.practicum.shareit.booking.model.BookingRequestState;

import java.util.Collection;

public interface BookingService {
    BookingOutgoingDto add(BookingIncomingDto bookingIncomingDto, Long bookerId);

    BookingOutgoingDto approve(Long bookingId, Boolean approved, Long userId);

    BookingOutgoingDto getById(Long bookingId, Long userId);

    Collection<BookingOutgoingDto> getAllByBookerId(Long bookerId, BookingRequestState state);

    Collection<BookingOutgoingDto> getAllByItemsOfUser(Long userId, BookingRequestState state);
}
