package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;

@Data
public class ItemDto {
    private Long id;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    private String description;
    private boolean available;
    private User owner;
}
