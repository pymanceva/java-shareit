package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated
    public ItemDto add(@RequestHeader(REQUEST_HEADER) Long userId, @Valid @RequestBody ItemDto itemDto) {

        log.info("POST/addItem");
        return itemService.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestHeader(REQUEST_HEADER) Long userId,
                          @RequestBody ItemDto itemDto) {
        log.info("PATCH/updateItem");
        return itemService.update(itemId, userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long itemId) {
        itemService.delete(itemId);
        log.info("DELETE/itemId");
    }

    @GetMapping
    public Collection<ItemDtoWithBookings> getAllByUserId(@RequestHeader(REQUEST_HEADER) Long userId,
                                              @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                              @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        log.info("GET/get-All-Items-By-UserId");
        return itemService.getAllByUserId(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookings getById(@PathVariable Long itemId, @RequestHeader(REQUEST_HEADER) Long userId) {
        log.info("GET/get-Item-By-Id");
        return itemService.getById(itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(String text,
                                      @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                      @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        log.info("GET/searchItem");
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestHeader(REQUEST_HEADER) Long userId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.info("POST/addComment");
        return itemService.addComment(userId, itemId, commentDto);
    }
}
