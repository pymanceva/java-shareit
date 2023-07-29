package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private Long id;
    private Item item;
    private String description;
    private User requester;
    private LocalDateTime created;
}
