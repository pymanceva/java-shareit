package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserStorageInMemory implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long userId;

    @Override
    public User add(User user) {
        if (this.isEmailUnique(user.getEmail())) {
            user.setId(++userId);
            users.put(user.getId(), user);
            log.info("User with id " + user.getId() + " has been added.");
        } else {
            log.error("User with email " + user.getEmail() + " already exists.");
            throw new ValidationException("User with email " + user.getEmail() + " already exists.");
        }
        return user;
    }

    @Override
    public User update(Long id, User user) {
        if (!users.containsKey(id)) {
            log.error("User with id " + id + " is not found.");
            throw new NotFoundException("User with id " + id + " is not found.");
        } else {
            User existed = users.get(id);
            if (user.getName() == null) {
                user.setName(existed.getName());
            }

            if (user.getEmail() == null) {
                user.setEmail(existed.getEmail());
            } else if (!user.getEmail().equals(existed.getEmail())) {
                if (!this.isEmailUnique(user.getEmail())) {
                    log.error("User with email " + users.get(id).getEmail() + " already exists.");
                    throw new ValidationException("User with email " + users.get(id).getEmail() + " already exists.");
                }
            }
            user.setId(existed.getId());
            users.replace(id, user);
            log.info("User with id " + id + " has been updated.");
        }
        return user;
    }

    @Override
    public void delete(Long id) {
        if (!users.containsKey(id)) {
            log.error("User with id " + id + " is not found.");
            throw new NotFoundException("User with id " + id + " is not found.");
        } else {
            log.info("User with id " + id + " has been deleted.");
            users.remove(id);
        }

    }

    @Override
    public Collection<User> getAll() {
        log.info("List of users has been gotten.");
        return users.values();
    }

    @Override
    public User getById(Long id) {
        if (!users.containsKey(id)) {
            log.error("User with id " + id + " is not found.");
            throw new NotFoundException("User with id " + id + " is not found.");
        }

        return users.get(id);
    }

    private boolean isEmailUnique(String email) {
        long result = users.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .count();

        return result == 0;
    }
}
