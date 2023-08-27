package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingRequestState;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(REQUEST_HEADER) Long userId,
                                      @Valid @RequestBody BookingIncomingDto bookingIncomingDto) {
        return bookingClient.add(userId, bookingIncomingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(REQUEST_HEADER) Long userId,
                                          @RequestParam("approved") Boolean approved,
                                          @PathVariable("bookingId") Long bookingId) {
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBookerId(@RequestHeader(REQUEST_HEADER) Long userId,
                                                   @RequestParam(name = "state", defaultValue = "ALL") BookingRequestState state,
                                                   @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) Integer from,
                                                   @RequestParam(value = "size", required = false, defaultValue = "10") @Min(1) Integer size) {
        return bookingClient.getAllByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByItemsOfUser(@RequestHeader(REQUEST_HEADER) Long userId,
                                                      @RequestParam(value = "state", defaultValue = "ALL") BookingRequestState state,
                                                      @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) Integer from,
                                                      @RequestParam(value = "size", required = false, defaultValue = "10") @Min(1) Integer size) {
        return bookingClient.getAllByItemsOfUser(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(REQUEST_HEADER) Long userId,
                                          @PathVariable Long bookingId) {
        return bookingClient.getById(userId, bookingId);
    }
}
