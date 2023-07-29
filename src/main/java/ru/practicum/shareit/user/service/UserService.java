package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto add(UserDto userDto);

    UserDto update(Long id, UserDto userDto);

    void delete(Long id);

    Collection<UserDto> getAll();

    UserDto getById(Long id);
}
