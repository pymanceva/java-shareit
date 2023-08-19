package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceIntegrationTest {
    private final UserDto userDto = new UserDto(
            null,
            "User1",
            "user1@yandex.ru");
    @Autowired
    private UserService userService;

    @Test
    void add() {
        UserDto result = userService.add(userDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals(userDto.getName(), result.getName());
        Assertions.assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    void update() {
        userService.add(userDto);
        UserDto userUpdated = new UserDto(1L, "Updated", "updated@ya.ru");

        UserDto result = userService.update(1L, userUpdated);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals(userUpdated.getName(), result.getName());
        Assertions.assertEquals(userUpdated.getEmail(), result.getEmail());
    }

    @Test
    void delete() {
        userService.add(userDto);

        userService.delete(1L);

        final NotFoundException ex = assertThrows(NotFoundException.class,
                () -> userService.getById(1L));

        assertThat("User id 1 is not found.", equalTo(ex.getMessage()));
    }

    @Test
    @Transactional(readOnly = true)
    void getAll() {
        userService.add(userDto);

        List<UserDto> result = (List<UserDto>) userService.getAll();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1, result.get(0).getId());
        Assertions.assertEquals(userDto.getName(), result.get(0).getName());
        Assertions.assertEquals(userDto.getEmail(), result.get(0).getEmail());
    }

    @Test
    @Transactional(readOnly = true)
    void getById() {
        userService.add(userDto);

        UserDto result = userService.getById(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(userDto.getName(), result.getName());
        Assertions.assertEquals(userDto.getEmail(), result.getEmail());
    }
}
