package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto add(ItemDto item, Long userId);

    ItemDto update(Long itemId, Long userId, ItemDto item);

    void delete(Long itemId);

    Collection<ItemDto> getAllByUserId(Long userId);

    ItemDto getByOwnerIdAndItemId(Long userId, Long itemId);

    ItemDto getById(Long itemId, Long userId);

    Collection<ItemDto> search(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
