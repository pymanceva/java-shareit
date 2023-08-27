package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingOutgoingDto;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingOutgoingDto add(@RequestHeader(REQUEST_HEADER) Long userId,
                                  @RequestBody BookingIncomingDto bookingIncomingDto) {
        log.info("POST/addBooking");
        return bookingService.add(bookingIncomingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutgoingDto approve(@RequestHeader(REQUEST_HEADER) Long userId,
                                      @PathVariable Long bookingId,
                                      @RequestParam Boolean approved) {
        log.info("PATCH/approve-Booking");
        return bookingService.approve(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingOutgoingDto getById(@PathVariable Long bookingId,
                                      @RequestHeader(REQUEST_HEADER) Long userId) {
        log.info("GET/get-Booking-By-Id");
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingOutgoingDto> getAllByBookerId(
            @RequestHeader(REQUEST_HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingRequestState state,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("GET/get-All-Bookings-By-UserId");
        return bookingService.getAllByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingOutgoingDto> getAllByItemsOfUser(
            @RequestHeader(REQUEST_HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingRequestState state,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("GET/get-All-Bookings-By-Items-Of-User");
        return bookingService.getAllByItemsOfUser(userId, state, from, size);
    }

}
