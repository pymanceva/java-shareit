package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated
    public ResponseEntity<Object> add(@RequestHeader(REQUEST_HEADER) Long userId,
                                      @Valid @RequestBody ItemDto itemDto) {

        log.info("POST/addItem");
        return itemClient.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable Long itemId,
                                         @RequestHeader(REQUEST_HEADER) Long userId,
                                         @RequestBody ItemDto itemDto) {
        log.info("PATCH/updateItem");
        return itemClient.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long itemId, @RequestHeader(REQUEST_HEADER) Long userId) {
        log.info("DELETE/requestId");
        itemClient.delete(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader(REQUEST_HEADER) Long userId,
                                                 @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                 @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        log.info("GET/get-All-Items-By-UserId");
        return itemClient.getAllByUserId(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@PathVariable Long itemId, @RequestHeader(REQUEST_HEADER) Long userId) {
        log.info("GET/get-Item-By-Id");
        return itemClient.getById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(REQUEST_HEADER) Long userId,
                                         String text,
                                         @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                         @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        log.info("GET/searchItem");
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @RequestHeader(REQUEST_HEADER) Long userId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("POST/addComment");
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
