package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";
    private final RequestClient requestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> add(@RequestHeader(REQUEST_HEADER) Long userId,
                                      @Valid @RequestBody RequestDto requestDto) {
        log.info("POST/addRequest");
        return requestClient.add(requestDto, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long requestId, @RequestHeader(REQUEST_HEADER) Long userId) {
        log.info("DELETE/requestId");
        requestClient.delete(requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsOfUser(@RequestHeader(REQUEST_HEADER) Long userId) {
        log.info("GET/getRequestsOfUser");
        return requestClient.getRequestsOfUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(REQUEST_HEADER) Long userId,
                                         @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) int from,
                                         @RequestParam(value = "size", required = false, defaultValue = "10") @Min(1) int size) {
        log.info("GET/get-All-Requests");
        return requestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(REQUEST_HEADER) Long userId, @PathVariable Long requestId) {
        log.info("GET/get-Request-By-Id");
        return requestClient.getById(userId, requestId);
    }
}
