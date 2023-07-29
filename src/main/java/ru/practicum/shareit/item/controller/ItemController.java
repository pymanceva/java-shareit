package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
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
public class ItemController {
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated
    public ItemDto add(@RequestHeader(REQUEST_HEADER) Long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestHeader(REQUEST_HEADER) Long userId,
                          @RequestBody ItemDto itemDto) {
        return itemService.update(itemId, userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(REQUEST_HEADER) Long userId, @PathVariable Long itemId) {
        itemService.delete(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllByUserId(@RequestHeader(REQUEST_HEADER) Long userId) {
        return itemService.getAllByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId, @RequestHeader(REQUEST_HEADER) Long userId) {
        return itemService.getById(itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(String text) {
        return itemService.search(text);

    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestHeader(REQUEST_HEADER) Long userId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
