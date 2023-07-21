package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    @Override
    public Item add(Item item, Long ownerId) {
        return itemStorage.add(item, ownerId);
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        return itemStorage.update(userId, itemId, item);
    }

    @Override
    public void delete(Long userId, Long itemId) {
        itemStorage.delete(userId, itemId);
    }

    @Override
    public Collection<Item> getAllByUserId(Long userId) {
        return itemStorage.getAllByUserId(userId);
    }

    @Override
    public Item getByOwnerIdAndItemId(Long userId, Long itemId) {
        return itemStorage.getByOwnerIdAndItemId(userId, itemId);
    }

    @Override
    public Item getById(Long itemId) {
        return itemStorage.getById(itemId);
    }

    @Override
    public Collection<Item> search(String text) {
        return itemStorage.search(text);
    }
}
