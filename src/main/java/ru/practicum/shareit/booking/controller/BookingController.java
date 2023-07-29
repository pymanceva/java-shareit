package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingOutgoingDto;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated
    public BookingOutgoingDto add(@RequestHeader(REQUEST_HEADER) Long userId, @Valid @RequestBody BookingIncomingDto bookingIncomingDto) {
        return bookingService.add(bookingIncomingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutgoingDto approve(@PathVariable Long bookingId,
                                      @RequestParam Boolean approved,
                                      @RequestHeader(REQUEST_HEADER) Long userId) {
        return bookingService.approve(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingOutgoingDto getById(@PathVariable Long bookingId,
                                      @RequestHeader(REQUEST_HEADER) Long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    Collection<BookingOutgoingDto> getAllByBookerId(
            @RequestHeader(REQUEST_HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingRequestState state) {
        return bookingService.getAllByBookerId(userId, state);
    }

    @GetMapping("/owner")
    Collection<BookingOutgoingDto> getAllByItemsOfUser(
            @RequestHeader(REQUEST_HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingRequestState state) {
        return bookingService.getAllByItemsOfUser(userId, state);
    }

}
