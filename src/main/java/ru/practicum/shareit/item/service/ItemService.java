package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item add(Item item, Long ownerId);

    Item update(Long userId, Long itemId, Item item);

    void delete(Long userId, Long itemId);

    Collection<Item> getAllByUserId(Long userId);

    Item getByOwnerIdAndItemId(Long userId, Long itemId);

    Item getById(Long itemId);

    Collection<Item> search(String text);
}
