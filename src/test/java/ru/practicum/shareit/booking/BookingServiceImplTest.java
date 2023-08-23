package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingOutgoingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private final User owner = new User(
            1L,
            "User1",
            "user1@yandex.ru");
    private final User booker = new User(
            2L,
            "User2",
            "user2@yandex.ru");
    private final UserDto bookerDto = new UserDto(
            2L,
            "User2",
            "user2@yandex.ru");
    private final Item item = new Item(
            1L,
            "Item1",
            "DescriptionItem1",
            true,
            owner,
            null);
    private final Booking booking = new Booking(
            1L,
            LocalDateTime.of(2023, 1, 1, 0, 0, 0),
            LocalDateTime.of(2023, 1, 1, 1, 1, 1),
            item,
            booker,
            BookingStatus.WAITING);
    private final ItemDto itemDto = new ItemDto(
            1L,
            "Item1",
            "DescriptionItem1",
            true,
            null);
    private final BookingIncomingDto bookingIncomingDto = new BookingIncomingDto(
            1L,
            LocalDateTime.of(2023, 1, 1, 0, 0, 0),
            LocalDateTime.of(2023, 1, 1, 1, 1, 1),
            1L,
            bookerDto,
            BookingStatus.WAITING);
    private final BookingOutgoingDto bookingOutgoingDto = new BookingOutgoingDto(
            1L,
            LocalDateTime.of(2023, 1, 1, 0, 0, 0),
            LocalDateTime.of(2023, 1, 1, 1, 1, 1),
            itemDto,
            bookerDto,
            BookingStatus.WAITING);
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    void addValid() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingOutgoingDto result = bookingService.add(bookingIncomingDto, 2L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookingOutgoingDto.getId(), result.getId());
        Assertions.assertEquals(bookingOutgoingDto.getStart(), result.getStart());
        Assertions.assertEquals(bookingOutgoingDto.getEnd(), result.getEnd());
        Assertions.assertEquals(bookingOutgoingDto.getItem().getId(), result.getItem().getId());
        Assertions.assertEquals(bookingOutgoingDto.getBooker().getId(), result.getBooker().getId());
        Assertions.assertEquals(bookingOutgoingDto.getStatus(), result.getStatus());

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void addInvalidAndThrow() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenThrow(new DataIntegrityViolationException("Booking was not saved."));

        final NotSavedException ex = assertThrows(NotSavedException.class,
                () -> bookingService.add(bookingIncomingDto, bookerDto.getId()));

        assertThat("Booking was not saved.", equalTo(ex.getMessage()));
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void addStartAfterEndAndThrow() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        bookingIncomingDto.setStart(LocalDateTime.of(2099,1,1,0,0));

        final NotAvailableException ex = assertThrows(NotAvailableException.class,
                () -> bookingService.add(bookingIncomingDto, bookerDto.getId()));

        assertThat("End of booking must be after start.", equalTo(ex.getMessage()));
    }

    @Test
    void addWhenItemNotFoundAndThrow() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.add(bookingIncomingDto, bookerDto.getId()));

        assertThat("Item with id 1 was not found.", equalTo(ex.getMessage()));
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void addWhenUserNotFoundAndThrow() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.add(bookingIncomingDto, bookerDto.getId()));

        assertThat("User with id 2 was not found.", equalTo(ex.getMessage()));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void addWhenNotAvailableAndThrow() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        item.setAvailable(false);

        final NotAvailableException ex = assertThrows(NotAvailableException.class,
                () -> bookingService.add(bookingIncomingDto, bookerDto.getId()));

        assertThat("Item " + item.getId() + " is not available for booking", equalTo(ex.getMessage()));
    }

    @Test
    void approve() {
        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingOutgoingDto result = bookingService.approve(1L, true, 1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookingOutgoingDto.getId(), result.getId());
        Assertions.assertEquals(bookingOutgoingDto.getStart(), result.getStart());
        Assertions.assertEquals(bookingOutgoingDto.getEnd(), result.getEnd());
        Assertions.assertEquals(bookingOutgoingDto.getItem().getId(), result.getItem().getId());
        Assertions.assertEquals(bookingOutgoingDto.getBooker().getId(), result.getBooker().getId());
        Assertions.assertEquals(BookingStatus.APPROVED, result.getStatus());

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void approveWhenBookingNotFoundAndThrow() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.approve(1L, true, 1L));

        assertThat("Booking with id 1 was not found.", equalTo(ex.getMessage()));
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void approveInvalidAndThrow() {
        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenThrow(new DataIntegrityViolationException("Booking was not approved."));
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        final NotSavedException ex = assertThrows(NotSavedException.class,
                () -> bookingService.approve(1L, true, 1L));

        assertThat("Booking was not approved.", equalTo(ex.getMessage()));
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void getById() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingOutgoingDto result = bookingService.getById(1L, 1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookingOutgoingDto.getId(), result.getId());
        Assertions.assertEquals(bookingOutgoingDto.getStart(), result.getStart());
        Assertions.assertEquals(bookingOutgoingDto.getEnd(), result.getEnd());
        Assertions.assertEquals(bookingOutgoingDto.getItem().getId(), result.getItem().getId());
        Assertions.assertEquals(bookingOutgoingDto.getBooker().getId(), result.getBooker().getId());

        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllByBookerIdStatusAll() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(bookingRepository.findAllByBookerIdOrderByEndDesc(anyLong(), any()))
                .thenReturn(List.of(booking));

        Collection<BookingOutgoingDto> result = bookingService.getAllByBookerId(2L, BookingRequestState.ALL, 0, 10);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByBookerIdStatusCurrent() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        any()))
                .thenReturn(List.of(booking));

        Collection<BookingOutgoingDto> result = bookingService.getAllByBookerId(2L, BookingRequestState.CURRENT, 0, 10);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByBookerIdStatusPast() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        anyLong(),
                        any(LocalDateTime.class),
                        any()))
                .thenReturn(List.of(booking));

        Collection<BookingOutgoingDto> result = bookingService.getAllByBookerId(2L, BookingRequestState.PAST, 0, 10);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByBookerIdStatusFuture() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                        anyLong(),
                        any(LocalDateTime.class),
                        any()))
                .thenReturn(List.of(booking));

        Collection<BookingOutgoingDto> result = bookingService.getAllByBookerId(2L, BookingRequestState.FUTURE, 0, 10);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByBookerIdStatusWaiting() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        anyLong(),
                        any(BookingStatus.class),
                        any()))
                .thenReturn(List.of(booking));

        Collection<BookingOutgoingDto> result = bookingService.getAllByBookerId(2L, BookingRequestState.WAITING, 0, 10);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByBookerIdStatusRejected() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Mockito
                .when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        anyLong(),
                        any(BookingStatus.class),
                        any()))
                .thenReturn(List.of(booking));

        Collection<BookingOutgoingDto> result = bookingService.getAllByBookerId(2L, BookingRequestState.REJECTED, 0, 10);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByBookerIdStatusIncorrect() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        Assertions.assertThrows(NotSupportedStatusException.class,
                () -> bookingService.getAllByBookerId(2L, BookingRequestState.UNSUPPORTED_STATUS, 0, 1));
    }

    @Test
    void getAllByItemsOfUserStatusAll() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        Mockito
                .when(bookingRepository.findAllByItemOwnerIdOrderByEndDesc(anyLong(), any()))
                .thenReturn(List.of(booking));

        Collection<BookingOutgoingDto> result = bookingService.getAllByItemsOfUser(1L, BookingRequestState.ALL, 0, 10);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByItemsOfUserStatusCurrent() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        Mockito
                .when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        any()))
                .thenReturn(List.of(booking));

        Collection<BookingOutgoingDto> result = bookingService.getAllByItemsOfUser(1L, BookingRequestState.CURRENT, 0, 10);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByItemsOfUserStatusPast() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        Mockito
                .when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        anyLong(),
                        any(LocalDateTime.class),
                        any()))
                .thenReturn(List.of(booking));

        Collection<BookingOutgoingDto> result = bookingService.getAllByItemsOfUser(1L, BookingRequestState.PAST, 0, 10);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByItemsOfUserStatusFuture() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        Mockito
                .when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        anyLong(),
                        any(LocalDateTime.class),
                        any()))
                .thenReturn(List.of(booking));

        Collection<BookingOutgoingDto> result = bookingService.getAllByItemsOfUser(1L, BookingRequestState.FUTURE, 0, 10);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByItemsOfUserStatusWaiting() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        Mockito
                .when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        anyLong(),
                        any(BookingStatus.class),
                        any()))
                .thenReturn(List.of(booking));

        Collection<BookingOutgoingDto> result = bookingService.getAllByItemsOfUser(1L, BookingRequestState.WAITING, 0, 10);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByItemsOfUserStatusRejected() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        Mockito
                .when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        anyLong(),
                        any(BookingStatus.class),
                        any()))
                .thenReturn(List.of(booking));

        Collection<BookingOutgoingDto> result = bookingService.getAllByItemsOfUser(1L, BookingRequestState.REJECTED, 0, 10);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getAllByItemsOfUserStatusIncorrect() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        Assertions.assertThrows(NotSupportedStatusException.class,
                () -> bookingService.getAllByItemsOfUser(1L, BookingRequestState.UNSUPPORTED_STATUS, 0, 1));
    }
}