package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;

import java.util.Collection;

public interface ItemService {
    ItemDto add(ItemDto item, Long userId);

    ItemDto update(Long itemId, Long userId, ItemDto item);

    void delete(Long itemId);

    Collection<ItemDtoWithBookings> getAllByUserId(Long userId, int from, int size);

    ItemDto getByOwnerIdAndItemId(Long userId, Long itemId);

    ItemDtoWithBookings getById(Long itemId, Long userId);

    Collection<ItemDto> search(String text, int from, int size);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
