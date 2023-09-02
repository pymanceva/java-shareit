package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RequestServiceIntegrationTest {
    private final UserDto requester = new UserDto(
            null,
            "User2",
            "user2@yandex.ru");
    private final RequestDto requestDto = new RequestDto(
            null,
            "Description",
            2L,
            LocalDateTime.of(2023, 1, 1, 0, 0),
            new ArrayList<>());

    @Autowired
    private UserService userService;
    @Autowired
    private RequestService requestService;

    @Test
    void add() {
        userService.add(requester);

        RequestDto result = requestService.add(requestDto, 1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals(requestDto.getDescription(), result.getDescription());
        Assertions.assertEquals(1, result.getRequesterId());
        Assertions.assertEquals(requestDto.getItems(), result.getItems());
    }

    @Test
    void delete() {
        userService.add(requester);
        requestService.add(requestDto, 1L);
        requestService.delete(1L, 1L);

        final NotFoundException ex = assertThrows(NotFoundException.class,
                () -> requestService.getById(1L, 1L));

        assertThat("Request id 1 was not found.", equalTo(ex.getMessage()));
    }

    @Test
    @Transactional(readOnly = true)
    void getRequestsOfUser() {
        userService.add(requester);
        requestService.add(requestDto, 1L);

        List<RequestDto> result = (List<RequestDto>) requestService.getRequestsOfUser(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1, result.get(0).getId());
        Assertions.assertEquals(requestDto.getDescription(), result.get(0).getDescription());
        Assertions.assertEquals(1, result.get(0).getRequesterId());
        Assertions.assertEquals(requestDto.getItems(), result.get(0).getItems());
    }

    @Test
    @Transactional(readOnly = true)
    void getById() {
        userService.add(requester);
        requestService.add(requestDto, 1L);

        RequestDto result = requestService.getById(1L, 1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(requestDto.getDescription(), result.getDescription());
        Assertions.assertEquals(1, result.getRequesterId());
        Assertions.assertEquals(requestDto.getItems(), result.getItems());
    }
}
