package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.NotSavedException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    private final User user = new User(
            1L,
            "User1",
            "user1@yandex.ru");
    private final UserDto userDto = new UserDto(
            1L,
            "User1",
            "user1@yandex.ru");
    private final Item item = new Item(
            1L,
            "Item1",
            "DescriptionItem1",
            true,
            user,
            null);
    private final Comment comment = new Comment(
            1L,
            "Text",
            item,
            user,
            LocalDateTime.of(2023, 1, 1, 1, 1, 1));
    private final Booking booking = new Booking(
            1L,
            LocalDateTime.of(2023, 1, 1, 0, 0, 0),
            LocalDateTime.of(2023, 1, 1, 1, 1, 1),
            item,
            user,
            BookingStatus.APPROVED);
    private final ItemDtoWithBookings itemDtoWithBookings = new ItemDtoWithBookings(
            1L,
            "Item1",
            "DescriptionItem1",
            true,
            null,
            BookingMapper.mapToBookingDtoForOwner(booking),
            null,
            new ArrayList<>());
    private final ItemDto itemDto = new ItemDto(
            1L,
            "Item1",
            "DescriptionItem1",
            true,
            null);
    private final CommentDto commentDto = new CommentDto(
            1L,
            "Text",
            "User1",
            LocalDateTime.of(2023, 1, 1, 1, 1, 1));

    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;

    @Test
    void addValid() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemDto result = itemService.add(itemDto, userDto.getId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(itemDto.getName(), result.getName());
        Assertions.assertEquals(itemDto.getDescription(), result.getDescription());
        Assertions.assertTrue(result.getAvailable());
        Assertions.assertEquals(itemDto.getRequestId(), result.getRequestId());

        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void addInvalidAndThrow() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenThrow(new DataIntegrityViolationException("Item was not saved."));

        final NotSavedException ex = assertThrows(NotSavedException.class,
                () -> itemService.add(itemDto, userDto.getId()));

        assertThat("Item was not saved " + itemDto, equalTo(ex.getMessage()));
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void addWhenUserNotFoundAndThrow() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemService.add(itemDto, userDto.getId()));

        assertThat("User id 1 was not found.", equalTo(ex.getMessage()));
    }

    @Test
    void updateValid() {
        Item itemUpdated = new Item(
                1L,
                "Item1",
                "DescriptionItem1",
                true,
                user,
                1L);
        ItemDto itemDtoUpdated = new ItemDto(
                1L,
                "Item1",
                "DescriptionItem1",
                true,
                1L);

        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(itemUpdated);
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemDto result = itemService.update(1L, 1L, itemDtoUpdated);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(result.getName(), itemUpdated.getName());
        Assertions.assertEquals(result.getDescription(), itemUpdated.getDescription());
        Assertions.assertEquals(result.getAvailable(), itemUpdated.getAvailable());
        Assertions.assertEquals(result.getRequestId(), itemUpdated.getRequestId());

        verify(itemRepository, times(1)).save(any(Item.class));
        verify(itemRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void updateInvalidAndThrow() {
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenThrow(new DataIntegrityViolationException("Item was not update."));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        final NotSavedException ex = assertThrows(NotSavedException.class,
                () -> itemService.update(1L, 1L, itemDto));

        assertThat("Item was not update " + itemDto, equalTo(ex.getMessage()));
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItemNotFoundAndThrow() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemService.update(1L, 1L, itemDto));

        assertThat("Item with id 1 was not found.", equalTo(ex.getMessage()));
    }

    @Test
    void delete() {
        itemService.delete(1L);

        verify(itemRepository, times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getAllByUserId() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(itemRepository.findAllByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(item));

        List<ItemDtoWithBookings> result = new ArrayList<>(itemService.getAllByUserId(1L, 0, 10));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1, result.get(0).getId());
        Assertions.assertEquals(result.get(0).getName(), item.getName());
        Assertions.assertEquals(result.get(0).getDescription(), item.getDescription());
        Assertions.assertEquals(result.get(0).getAvailable(), item.getAvailable());
        Assertions.assertEquals(result.get(0).getRequestId(), item.getRequestId());

        verify(itemRepository, times(1)).findAllByOwnerId(anyLong(), any(Pageable.class));
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getByOwnerIdAndItemId() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemDto result = itemService.getByOwnerIdAndItemId(1L, 1L);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(itemDto.getName(), result.getName());
        Assertions.assertEquals(itemDto.getDescription(), result.getDescription());
        Assertions.assertTrue(result.getAvailable());
        Assertions.assertEquals(itemDto.getRequestId(), result.getRequestId());

        verify(itemRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getByOwnerIdAndItemIdWhenItemNotFoundAndThrow() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getByOwnerIdAndItemId(1L, 1L));

        assertThat("Item id 1 was not found.", equalTo(exception.getMessage()));
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void getByOwnerIdAndItemIdWhenUserNotOwnerAndThrow() {
        User owner = new User(22L, "name", "email@ya.ru");
        item.setOwner(owner);

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        final NotOwnerException exception = assertThrows(NotOwnerException.class,
                () -> itemService.getByOwnerIdAndItemId(1L, 1L));

        assertThat("Item id 1 does not belong to user id 1", equalTo(exception.getMessage()));
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void getById() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(bookingRepository.findFirstByItemIdAndStatusAndStartIsBeforeOrderByStartDesc(
                        anyLong(),
                        any(BookingStatus.APPROVED.getDeclaringClass()),
                        any(LocalDateTime.class)))
                .thenReturn(booking);

        Mockito
                .when(bookingRepository.findFirstByItemIdAndStatusAndStartIsAfterOrderByStartAsc(
                        anyLong(),
                        any(BookingStatus.APPROVED.getDeclaringClass()),
                        any(LocalDateTime.class)))
                .thenReturn(null);

        ItemDtoWithBookings result = itemService.getById(1L, 1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(itemDtoWithBookings.getName(), result.getName());
        Assertions.assertEquals(itemDtoWithBookings.getDescription(), result.getDescription());
        Assertions.assertTrue(result.getAvailable());
        Assertions.assertEquals(itemDto.getRequestId(), result.getRequestId());

        verify(itemRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(itemRepository);
        verify(commentRepository, times(1)).findAllByItemId(anyLong(), any());
        verifyNoMoreInteractions(commentRepository);
        verify(bookingRepository, times(1))
                .findFirstByItemIdAndStatusAndStartIsBeforeOrderByStartDesc(
                        anyLong(),
                        any(BookingStatus.APPROVED.getDeclaringClass()),
                        any(LocalDateTime.class));
        verify(bookingRepository, times(1)).findFirstByItemIdAndStatusAndStartIsAfterOrderByStartAsc(
                anyLong(),
                any(BookingStatus.APPROVED.getDeclaringClass()),
                any(LocalDateTime.class));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getByIdWhenNotFoundAndThrow() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getById(1L, 1L));

        assertThat("Item with id 1 was not found.", equalTo(exception.getMessage()));
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void search() {
        Mockito.when(itemRepository.search(anyString(), any())).thenReturn(List.of(item));

        List<ItemDto> found = (List<ItemDto>) itemService.search("Item1", 0, 1);


        Assertions.assertNotNull(found);
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals(1L, found.get(0).getId());
        Assertions.assertEquals(item.getName(), found.get(0).getName());
        Assertions.assertEquals(item.getDescription(), found.get(0).getDescription());
        Assertions.assertTrue(found.get(0).getAvailable());
    }

    @Test
    void searchWhenTextBlank() {
        List<ItemDto> result = (List<ItemDto>) itemService.search(" ", 0, 1);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void addComment() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingRepository
                        .findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any()))
                .thenReturn(List.of(booking));
        Mockito
                .when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto result = itemService.addComment(1L, 1L, commentDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(comment.getText(), result.getText());
        Assertions.assertEquals(comment.getId(), result.getId());
        Assertions.assertEquals(comment.getCreated().toString(), result.getCreated().toString());
        Assertions.assertEquals(comment.getAuthor().getName(), result.getAuthorName());

        verify(bookingRepository, times(1))
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any());
        verifyNoMoreInteractions(bookingRepository);
        verify(commentRepository, times(1)).save(any(Comment.class));
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void addCommentInvalidAndThrow() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingRepository
                        .findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any()))
                .thenReturn(List.of(booking));
        Mockito
                .when(commentRepository.save(any(Comment.class)))
                .thenThrow(new DataIntegrityViolationException(""));

        final NotSavedException exception = assertThrows(NotSavedException.class,
                () -> itemService.addComment(1L, 1L, commentDto));

        assertThat("Comment was not created.", equalTo(exception.getMessage()));
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void addCommentWhenItemNotFoundAndThrow() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(1L, 1L, commentDto));

        assertThat("Item with id 1 was not found.", equalTo(exception.getMessage()));
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void addCommentWhenUserNotFoundAndThrow() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(1L, 1L, commentDto));

        assertThat("User id 1 was not found.", equalTo(exception.getMessage()));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void addCommentWhenUserDidNotBookItemAndThrow() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingRepository
                        .findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any()))
                .thenReturn(new ArrayList<>());

        final NotAvailableException exception = assertThrows(NotAvailableException.class,
                () -> itemService.addComment(1L, 1L, commentDto));

        assertThat("Cannot comment item you have never booked.", equalTo(exception.getMessage()));
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any());
    }
}