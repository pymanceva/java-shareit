package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemStorageInMemory implements ItemStorage {
    private final Map<Long, List<Item>> itemsByUsers = new HashMap<>();
    private final Map<Long, Item> items = new HashMap<>();
    private final UserService userService;
    private long itemId;

    @Override
    public Item add(Item item, Long userId) {
        if (userService.getById(userId) == null) {
            log.error("User id " + userId + " does not exist.");
            throw new NotFoundException("User id " + userId + " does not exist.");
        }
        item.setId(++itemId);
        item.setOwner(userService.getById(userId));
        List<Item> itemsOfUser = new ArrayList<>();
        if (itemsByUsers.containsKey(userId)) {
            itemsOfUser = itemsByUsers.get(userId);
        }
        itemsOfUser.add(item);
        itemsByUsers.put(userId, itemsOfUser);
        items.put(item.getId(), item);
        log.info("Item with id " + item.getId() + " has been added by user " + userId + ".");
        return item;
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        if (!itemsByUsers.containsKey(userId)) {
            log.error("Items of user with id " + userId + " is not found.");
            throw new NotFoundException("Items of user with id " + userId + " is not found.");
        }

        List<Item> itemsOfUser = new ArrayList<>();
        if (itemsByUsers.containsKey(userId)) {
            itemsOfUser = itemsByUsers.get(userId);
        }

        Item existed = getByOwnerIdAndItemId(userId, itemId);

        if (existed == null) {
            throw new NotFoundException("Item id " + itemId + " of user with id " + userId + " is not found.");
        }

        if (item.getName() == null) {
            item.setName(existed.getName());
        }

        if (item.getDescription() == null) {
            item.setDescription(existed.getDescription());
        }

        if (item.getAvailable() == null) {
            item.setAvailable(existed.getAvailable());
        }

        item.setId(existed.getId());
        itemsOfUser.remove(existed);
        itemsOfUser.add(item);
        itemsByUsers.replace(userId, itemsOfUser);
        items.replace(item.getId(), item);
        log.info("Item with id " + itemId + " has been updated.");

        return item;
    }

    @Override
    public void delete(Long userId, Long itemId) {
        if (!itemsByUsers.containsKey(userId)) {
            log.error("Items of user with id " + userId + " is not found.");
            throw new NotFoundException("Items of user with id " + userId + " is not found.");
        } else {
            List<Item> itemsOfUser = itemsByUsers.get(userId);
            Item existed = getByOwnerIdAndItemId(userId, itemId);
            log.info("Item with id " + itemId + " has been deleted.");
            itemsOfUser.remove(existed);
            items.remove(itemId);
            itemsByUsers.replace(userId, itemsOfUser);
        }
    }

    @Override
    public Collection<Item> getAllByUserId(Long userId) {
        log.info("List of items of user id " + userId + " has been gotten.");
        return itemsByUsers.get(userId);
    }

    @Override
    public Item getByOwnerIdAndItemId(Long ownerId, Long itemId) {
        return itemsByUsers.getOrDefault(ownerId, new ArrayList<>())
                .stream()
                .filter(i -> i.getId().equals(itemId))
                .findAny()
                .orElseThrow(() -> new NotFoundException("Item id " + itemId + " does not exist."));
    }

    @Override
    public Item getById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Collection<Item> search(String text) {
        if (text.isBlank()) {
            return new HashSet<>();
        }

        List<Item> searchByName = items.values()
                .stream()
                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase()))
                .filter(i -> i.getAvailable().equals(Boolean.TRUE))
                .collect(Collectors.toList());

        List<Item> searchByDescription = items.values()
                .stream()
                .filter(i -> i.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(i -> i.getAvailable().equals(Boolean.TRUE))
                .collect(Collectors.toList());

        Set<Item> result = Stream.concat(searchByName.stream(), searchByDescription.stream()).collect(Collectors.toSet());
        return result.stream().sorted(Comparator.comparingLong(Item::getId)).collect(Collectors.toList());
    }
}
