package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final String REQUEST_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item add(@RequestHeader(REQUEST_HEADER) Long userId, @Valid @RequestBody Item item) {
        return itemService.add(item, userId);
    }

    @PatchMapping("/{itemId}")
    public Item update(@PathVariable Long itemId,
                       @RequestHeader(REQUEST_HEADER) Long userId,
                       @RequestBody Item item) {
        return itemService.update(userId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(REQUEST_HEADER) Long userId, @PathVariable Long itemId) {
        itemService.delete(userId, itemId);
    }

    @GetMapping
    public Collection<Item> getAllByUserId(@RequestHeader(REQUEST_HEADER) Long userId) {
        return itemService.getAllByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public Item getById(@PathVariable Long itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping("/search")
    public Collection<Item> search(String text) {
        return itemService.search(text);
    }
}
