package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotSavedException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto add(UserDto userDto) {
        try {
            User user = userRepository.save(UserMapper.mapToUser(userDto));
            log.info("New user id " + user.getId() + " has been added.");
            return UserMapper.mapToUserDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException("User was not save " + userDto);
        }
    }

    @Transactional
    @Override
    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User with id " + id + " was not found."));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        try {
            log.info("Existed user id " + user.getId() + " has been updated.");
            return UserMapper.mapToUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException("User was not save " + userDto);
        }
    }

    @Transactional
    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
        log.info("Existed user id " + id + " has been deleted.");
    }

    @Override
    public Collection<UserDto> getAll() {
        log.info("List of all users has been gotten.");
        return UserMapper.mapToUserDto(userRepository.findAll());
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User id " + id + " is not found."));
        log.info("User id " + id + " has been gotten.");
        return UserMapper.mapToUserDto(user);
    }
}
