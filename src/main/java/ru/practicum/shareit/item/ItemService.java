package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;

    public Item add(Item item, Long ownerId) {
        return itemStorage.add(item, ownerId);
    }

    public Item update(Long userId, Long itemId, Item item) {
        return itemStorage.update(userId, itemId, item);
    }

    public void delete(Long userId, Long itemId) {
        itemStorage.delete(userId, itemId);
    }

    public Collection<Item> getAllByUserId(Long userId) {
        return itemStorage.getAllByUserId(userId);
    }

    public Item getByOwnerIdAndItemId(Long userId, Long itemId) {
        return itemStorage.getByOwnerIdAndItemId(userId, itemId);
    }

    public Item getById(Long itemId) {
        return itemStorage.getById(itemId);
    }

    public Collection<Item> search(String text) {
        return itemStorage.search(text);
    }
}
