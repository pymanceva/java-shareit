package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@RequestBody @Valid UserDto userDto) {
        return userService.add(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @RequestBody UserDto userDto) {
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        userService.delete(id);
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        return userService.getById(id);
    }

}
