package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserStorage {
    User add(User user);

    User update(Long id, User user);

    void delete(Long id);

    Collection<User> getAll();

    User getById(Long id);
}
