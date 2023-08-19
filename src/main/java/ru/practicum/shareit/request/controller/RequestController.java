package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto add(@RequestHeader(REQUEST_HEADER) Long userId, @RequestBody @Valid RequestDto requestDto) {
        log.info("POST/addRequest");
        return requestService.add(requestDto, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long requestId, @RequestHeader(REQUEST_HEADER) Long userId) {
        log.info("DELETE/requestId");
        requestService.delete(requestId, userId);
    }

    @GetMapping
    public Collection<RequestDto> getRequestsOfUser(@RequestHeader(REQUEST_HEADER) Long userId) {
        log.info("GET/getRequestsOfUser");
        return requestService.getRequestsOfUser(userId);
    }

    @GetMapping("/all")
    public Collection<RequestDto> getAll(@RequestHeader(REQUEST_HEADER) Long userId,
                                         @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) int from,
                                         @RequestParam(value = "size", required = false, defaultValue = "10") @Min(1) int size) {
        log.info("GET/get-All-Requests");
        return requestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestDto getById(@RequestHeader(REQUEST_HEADER) Long userId, @PathVariable Long requestId) {
        log.info("GET/get-Request-By-Id");
        return requestService.getById(userId, requestId);
    }
}
