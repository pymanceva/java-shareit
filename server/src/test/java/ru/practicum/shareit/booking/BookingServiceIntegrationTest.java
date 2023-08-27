package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingOutgoingDto;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotSupportedStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingServiceIntegrationTest {
    private final UserDto owner = new UserDto(
            null,
            "User1",
            "user1@yandex.ru");
    private final UserDto bookerDto = new UserDto(
            null,
            "User2",
            "user2@yandex.ru");
    private final ItemDto itemDto = new ItemDto(
            null,
            "Item1",
            "DescriptionItem1",
            true,
            null);
    private final BookingIncomingDto bookingIncomingDto = new BookingIncomingDto(
            null,
            LocalDateTime.of(2023, 1, 1, 0, 0, 0),
            LocalDateTime.of(2023, 1, 1, 1, 1, 1),
            1L,
            bookerDto,
            BookingStatus.WAITING);

    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    @Test
    void add() {
        userService.add(owner);
        itemService.add(itemDto, 1L);
        userService.add(bookerDto);

        BookingOutgoingDto result = bookingService.add(bookingIncomingDto, 2L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(bookingIncomingDto.getStart(), result.getStart());
        Assertions.assertEquals(bookingIncomingDto.getEnd(), result.getEnd());
        Assertions.assertEquals(bookingIncomingDto.getItemId(), result.getItem().getId());
        Assertions.assertEquals(2, result.getBooker().getId());
        Assertions.assertEquals(bookingIncomingDto.getStatus(), result.getStatus());
    }

    @Test
    void approve() {
        userService.add(owner);
        itemService.add(itemDto, 1L);
        userService.add(bookerDto);
        bookingService.add(bookingIncomingDto, 2L);

        BookingOutgoingDto result = bookingService.approve(1L, true, 1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(bookingIncomingDto.getStart(), result.getStart());
        Assertions.assertEquals(bookingIncomingDto.getEnd(), result.getEnd());
        Assertions.assertEquals(bookingIncomingDto.getItemId(), result.getItem().getId());
        Assertions.assertEquals(2, result.getBooker().getId());
        Assertions.assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void getById() {
        userService.add(owner);
        itemService.add(itemDto, 1L);
        userService.add(bookerDto);
        bookingService.add(bookingIncomingDto, 2L);

        BookingOutgoingDto result = bookingService.getById(1L, 2L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(bookingIncomingDto.getStart(), result.getStart());
        Assertions.assertEquals(bookingIncomingDto.getEnd(), result.getEnd());
        Assertions.assertEquals(bookingIncomingDto.getItemId(), result.getItem().getId());
        Assertions.assertEquals(2, result.getBooker().getId());
        Assertions.assertEquals(bookingIncomingDto.getStatus(), result.getStatus());
    }

    @Test
    void getAllByBookerIdStatusAll() {
        userService.add(owner);
        itemService.add(itemDto, 1L);
        userService.add(bookerDto);
        bookingService.add(bookingIncomingDto, 2L);

        List<BookingOutgoingDto> result = (List<BookingOutgoingDto>) bookingService.getAllByBookerId(
                2L, BookingRequestState.ALL, 0, 10);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByBookerIdStatusPast() {
        userService.add(owner);
        itemService.add(itemDto, 1L);
        userService.add(bookerDto);
        bookingService.add(bookingIncomingDto, 2L);

        List<BookingOutgoingDto> result = (List<BookingOutgoingDto>) bookingService.getAllByBookerId(
                2L, BookingRequestState.PAST, 0, 10);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByBookerIdStatusWaiting() {
        userService.add(owner);
        itemService.add(itemDto, 1L);
        userService.add(bookerDto);
        bookingService.add(bookingIncomingDto, 2L);

        List<BookingOutgoingDto> result = (List<BookingOutgoingDto>) bookingService.getAllByBookerId(
                2L, BookingRequestState.WAITING, 0, 10);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByBookerIdStatusIncorrect() {
        userService.add(owner);
        itemService.add(itemDto, 1L);
        userService.add(bookerDto);
        bookingService.add(bookingIncomingDto, 2L);

        Assertions.assertThrows(NotSupportedStatusException.class,
                () -> bookingService.getAllByBookerId(2L, BookingRequestState.UNSUPPORTED_STATUS, 0, 1));
    }

    @Test
    void getAllByItemsOfUserStatusAll() {
        userService.add(owner);
        itemService.add(itemDto, 1L);
        userService.add(bookerDto);
        bookingService.add(bookingIncomingDto, 2L);

        List<BookingOutgoingDto> result = (List<BookingOutgoingDto>) bookingService.getAllByItemsOfUser(
                1L, BookingRequestState.ALL, 0, 10);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByItemsOfUserStatusPast() {
        userService.add(owner);
        itemService.add(itemDto, 1L);
        userService.add(bookerDto);
        bookingService.add(bookingIncomingDto, 2L);

        List<BookingOutgoingDto> result = (List<BookingOutgoingDto>) bookingService.getAllByItemsOfUser(
                1L, BookingRequestState.PAST, 0, 10);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByItemsOfUserStatusWaiting() {
        userService.add(owner);
        itemService.add(itemDto, 1L);
        userService.add(bookerDto);
        bookingService.add(bookingIncomingDto, 2L);

        List<BookingOutgoingDto> result = (List<BookingOutgoingDto>) bookingService.getAllByItemsOfUser(
                1L, BookingRequestState.WAITING, 0, 10);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByItemsOfUserStatusIncorrect() {
        userService.add(owner);
        itemService.add(itemDto, 1L);
        userService.add(bookerDto);
        bookingService.add(bookingIncomingDto, 2L);

        Assertions.assertThrows(NotSupportedStatusException.class,
                () -> bookingService.getAllByItemsOfUser(1L, BookingRequestState.UNSUPPORTED_STATUS, 0, 1));
    }
}