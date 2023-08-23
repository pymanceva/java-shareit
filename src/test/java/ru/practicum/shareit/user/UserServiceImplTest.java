package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotSavedException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private final User user = new User(
            1L,
            "User1",
            "user1@yandex.ru");
    private final UserDto userDto = new UserDto(
            1L,
            "User1",
            "user1@yandex.ru");
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    @Test
    void addValid() {
        Mockito
                .when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto result = userService.add(userDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(userDto.getName(), result.getName());
        Assertions.assertEquals(userDto.getEmail(), result.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void addInvalidAndThrow() {
        Mockito
                .when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("User was not saved."));

        final NotSavedException ex = assertThrows(NotSavedException.class,
                () -> userService.add(userDto));

        assertThat("User was not save " + userDto, equalTo(ex.getMessage()));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateValid() {
        User updatedUser = new User(
                1L,
                "User1Updated",
                "user1updated@yandex.ru");
        UserDto updatedUserDto = new UserDto(
                1L,
                "User1Updated",
                "user1updated@yandex.ru");

        Mockito
                .when(userRepository.save(any(User.class)))
                .thenReturn(updatedUser);
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        UserDto result = userService.update(1L, updatedUserDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(result.getName(), updatedUser.getName());
        Assertions.assertEquals(result.getEmail(), updatedUser.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateInvalidAndThrow() {
        Mockito
                .when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("User was not save."));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        final NotSavedException ex = assertThrows(NotSavedException.class,
                () -> userService.update(1L, userDto));

        assertThat("User was not save " + userDto, equalTo(ex.getMessage()));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateWhenUserNotFoundAndThrow() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException ex = assertThrows(NotFoundException.class,
                () -> userService.update(1L, userDto));

        assertThat("User with id 1 was not found.", equalTo(ex.getMessage()));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void delete() {
        userService.delete(1L);

        verify(userRepository, times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAll() {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(List.of(user));

        List<UserDto> result = new ArrayList<>(userService.getAll());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1, result.get(0).getId());
        Assertions.assertEquals(userDto.getName(), result.get(0).getName());
        Assertions.assertEquals(userDto.getEmail(), result.get(0).getEmail());

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getById() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        UserDto result = userService.getById(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, user.getId());
        Assertions.assertEquals(user.getName(), result.getName());
        Assertions.assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getByIdWhenUserNotFoundAndThrow() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getById(1L));

        assertThat("User id 1 is not found.", equalTo(exception.getMessage()));
        verify(userRepository, times(1)).findById(anyLong());
    }
}