package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
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
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto add(UserDto userDto) {
        try {
            User user = userRepository.save(UserMapper.mapToUser(userDto));
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

        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Collection<UserDto> getAll() {
        return UserMapper.mapToUserDto(userRepository.findAll());
    }

    @Override
    public UserDto getById(Long id) {
        return UserMapper.mapToUserDto(userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User id " + id + " is not found.")));
    }
}
