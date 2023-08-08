package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
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

        log.info("Income request to add new item.");
        return itemService.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestHeader(REQUEST_HEADER) Long userId,
                          @RequestBody ItemDto itemDto) {
        log.info("Income request to update existed item.");
        return itemService.update(itemId, userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(REQUEST_HEADER) Long userId, @PathVariable Long itemId) {
        itemService.delete(itemId);
        log.info("Income request to delete existed item.");
    }

    @GetMapping
    public Collection<ItemDto> getAllByUserId(@RequestHeader(REQUEST_HEADER) Long userId) {
        log.info("Income request to get all items of user.");
        return itemService.getAllByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId, @RequestHeader(REQUEST_HEADER) Long userId) {
        log.info("Income request to get item by id.");
        return itemService.getById(itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(String text) {
        log.info("Income request to search item by text.");
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestHeader(REQUEST_HEADER) Long userId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.info("Income request to add comment for item.");
        return itemService.addComment(userId, itemId, commentDto);
    }
}
