package ru.practicum.shareit.item;

import java.util.Collection;

public interface ItemStorage {
    Item add(Item item, Long ownerId);

    Item update(Long userId, Long itemId, Item item);

    void delete(Long userId, Long itemId);

    Collection<Item> getAllByUserId(Long userId);

    Item getByOwnerIdAndItemId(Long ownerId, Long itemId);

    Item getById(Long itemId);

    Collection<Item> search(String text);
}
