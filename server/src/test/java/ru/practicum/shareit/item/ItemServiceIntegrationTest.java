package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.ItemService;
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
public class ItemServiceIntegrationTest {
    private final UserDto owner = new UserDto(
            null,
            "User1",
            "user1@yandex.ru");
    private final UserDto bookerDto = new UserDto(
            null,
            "User2",
            "user2@yandex.ru");
    private final ItemDto itemDtoWithoutRequest = new ItemDto(
            null,
            "Item1",
            "DescriptionItem1",
            true,
            null);
    private final ItemDto itemDtoWithRequest = new ItemDto(
            1L,
            "Item1",
            "DescriptionItem1",
            true,
            1L);
    private final RequestDto requestDto = new RequestDto(
            null,
            "Description",
            2L,
            LocalDateTime.of(2023, 1, 1, 0, 0),
            new ArrayList<>());

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private RequestService requestService;

    @Test
    void addItemWithoutRequest() {
        userService.add(owner);
        ItemDto resultItem = itemService.add(itemDtoWithoutRequest, 1L);

        Assertions.assertEquals(1L, resultItem.getId());
        Assertions.assertEquals(itemDtoWithoutRequest.getName(), resultItem.getName());
        Assertions.assertEquals(itemDtoWithoutRequest.getDescription(), resultItem.getDescription());
        Assertions.assertEquals(itemDtoWithoutRequest.getAvailable(), resultItem.getAvailable());
    }

    @Test
    void addItemToRequest() {
        userService.add(owner);
        userService.add(bookerDto);
        requestService.add(requestDto, 2L);

        ItemDto resultItem = itemService.add(itemDtoWithRequest, 1L);

        Assertions.assertEquals(1L, resultItem.getRequestId());
        Assertions.assertEquals(itemDtoWithRequest.getName(), resultItem.getName());
        Assertions.assertEquals(itemDtoWithRequest.getDescription(), resultItem.getDescription());
        Assertions.assertEquals(itemDtoWithRequest.getAvailable(), resultItem.getAvailable());
        Assertions.assertEquals(itemDtoWithRequest.getRequestId(), resultItem.getRequestId());
    }

    @Test
    void update() {
        userService.add(owner);
        itemService.add(itemDtoWithoutRequest, 1L);

        ItemDto updateItem = new ItemDto(1L, "Update", "Description", true, null);

        ItemDto resultUpdatedItem = itemService.update(1L, 1L, updateItem);

        Assertions.assertEquals(1L, resultUpdatedItem.getId());
        Assertions.assertEquals(updateItem.getName(), resultUpdatedItem.getName());
        Assertions.assertEquals(updateItem.getDescription(), resultUpdatedItem.getDescription());
        Assertions.assertEquals(updateItem.getAvailable(), resultUpdatedItem.getAvailable());
        Assertions.assertEquals(updateItem.getRequestId(), resultUpdatedItem.getRequestId());
    }

    @Test
    void delete() {
        userService.add(owner);
        itemService.add(itemDtoWithoutRequest, 1L);

        itemService.delete(1L);

        final NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemService.getById(1L, 1L));

        assertThat("Item with id 1 was not found.", equalTo(ex.getMessage()));
    }

    @Test
    @Transactional(readOnly = true)
    void getAllByUserId() {
        userService.add(owner);
        itemService.add(itemDtoWithoutRequest, 1L);

        List<ItemDtoWithBookings> result = (List<ItemDtoWithBookings>) itemService.getAllByUserId(1L, 0, 10);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(itemDtoWithoutRequest.getName(), result.get(0).getName());
        Assertions.assertEquals(itemDtoWithoutRequest.getDescription(), result.get(0).getDescription());
        Assertions.assertEquals(itemDtoWithoutRequest.getAvailable(), result.get(0).getAvailable());
        Assertions.assertEquals(itemDtoWithoutRequest.getRequestId(), result.get(0).getRequestId());
    }

    @Test
    @Transactional(readOnly = true)
    void getByOwnerIdAndItemId() {
        userService.add(owner);
        itemService.add(itemDtoWithoutRequest, 1L);

        ItemDto result = itemService.getByOwnerIdAndItemId(1L, 1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(itemDtoWithoutRequest.getName(), result.getName());
        Assertions.assertEquals(itemDtoWithoutRequest.getDescription(), result.getDescription());
        Assertions.assertEquals(itemDtoWithoutRequest.getAvailable(), result.getAvailable());
        Assertions.assertEquals(itemDtoWithoutRequest.getRequestId(), result.getRequestId());
    }

    @Test
    @Transactional(readOnly = true)
    void getById() {
        userService.add(owner);
        itemService.add(itemDtoWithoutRequest, 1L);

        ItemDtoWithBookings result = itemService.getById(1L, 1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(itemDtoWithoutRequest.getName(), result.getName());
        Assertions.assertEquals(itemDtoWithoutRequest.getDescription(), result.getDescription());
        Assertions.assertEquals(itemDtoWithoutRequest.getAvailable(), result.getAvailable());
        Assertions.assertEquals(itemDtoWithoutRequest.getRequestId(), result.getRequestId());
    }

    @Test
    @Transactional(readOnly = true)
    void search() {
        userService.add(owner);
        itemService.add(itemDtoWithoutRequest, 1L);

        List<ItemDto> result = (List<ItemDto>) itemService.search("item", 0, 10);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(itemDtoWithoutRequest.getName(), result.get(0).getName());
        Assertions.assertEquals(itemDtoWithoutRequest.getDescription(), result.get(0).getDescription());
        Assertions.assertEquals(itemDtoWithoutRequest.getAvailable(), result.get(0).getAvailable());
        Assertions.assertEquals(itemDtoWithoutRequest.getRequestId(), result.get(0).getRequestId());
    }
}
