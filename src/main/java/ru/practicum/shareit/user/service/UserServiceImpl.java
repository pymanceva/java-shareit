package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User add(User user) {
        return userStorage.add(user);
    }

    @Override
    public User update(Long id, User user) {
        return userStorage.update(id, user);
    }

    @Override
    public void delete(Long id) {
        userStorage.delete(id);
    }

    @Override
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User getById(Long id) {
        return userStorage.getById(id);
    }

}
