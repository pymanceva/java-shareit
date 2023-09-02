package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.NotSavedException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    private final User user = new User(
            1L,
            "User1",
            "user1@yandex.ru");
    private final Request request = new Request(
            1L,
            "Description",
            user,
            LocalDateTime.of(2023, 1, 1, 0, 0),
            new ArrayList<>());
    private final RequestDto requestDto = new RequestDto(
            1L,
            "Description",
            1L,
            LocalDateTime.of(2023, 1, 1, 0, 0),
            new ArrayList<>());

    @InjectMocks
    private RequestServiceImpl requestService;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    void addValid() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        RequestDto result = requestService.add(requestDto, 1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(requestDto.getId(), result.getId());
        Assertions.assertEquals(requestDto.getDescription(), result.getDescription());
        Assertions.assertEquals(requestDto.getRequesterId(), result.getRequesterId());
        Assertions.assertEquals(requestDto.getItems(), result.getItems());

        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void addInvalidAndThrow() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(requestRepository.save(any(Request.class)))
                .thenThrow(new DataIntegrityViolationException("Request was not saved."));

        NotSavedException ex = assertThrows(NotSavedException.class,
                () -> requestService.add(requestDto, user.getId()));

        assertThat("Request was not save " + requestDto, equalTo(ex.getMessage()));
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void addWhenUserNotFoundAndThrow() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> requestService.add(requestDto, user.getId()));

        assertThat("User id 1 was not found.", equalTo(ex.getMessage()));
    }

    @Test
    void deleteByNotOwnerAndThrow() {
        Mockito
                .when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));

        NotOwnerException ex = assertThrows(NotOwnerException.class,
                () -> requestService.delete(1L, 2L));

        assertThat("Request can by deleted by requester only.", equalTo(ex.getMessage()));
    }

    @Test
    void deleteByOwner() {
        Mockito
                .when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));

        requestService.delete(1L, 1L);

        verify(requestRepository, times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(requestRepository);
    }

    @Test
    void deleteWhenUserNotFoundAndThrow() {
        Mockito
                .when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> requestService.delete(1L, 2L));

        assertThat("Request id 1 was not found.", equalTo(ex.getMessage()));
    }

    @Test
    void getAll() {
        Mockito
                .when(requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(request));

        List<RequestDto> result = new ArrayList<>(requestService.getAll(user.getId(), 0, 10));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1, result.get(0).getId());
        Assertions.assertEquals(requestDto.getDescription(), result.get(0).getDescription());
        Assertions.assertEquals(requestDto.getRequesterId(), result.get(0).getRequesterId());
        Assertions.assertEquals(requestDto.getCreated(), result.get(0).getCreated());
        Assertions.assertEquals(requestDto.getItems(), result.get(0).getItems());

        verify(requestRepository, times(1))
                .findAllByRequesterIdNotOrderByCreatedDesc(anyLong(), any(Pageable.class));
        verifyNoMoreInteractions(requestRepository);
    }

    @Test
    void getRequestsOfUser() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(request));

        List<RequestDto> result = new ArrayList<>(requestService.getRequestsOfUser(user.getId()));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1, result.get(0).getId());
        Assertions.assertEquals(requestDto.getDescription(), result.get(0).getDescription());
        Assertions.assertEquals(requestDto.getRequesterId(), result.get(0).getRequesterId());
        Assertions.assertEquals(requestDto.getCreated(), result.get(0).getCreated());
        Assertions.assertEquals(requestDto.getItems(), result.get(0).getItems());

        verify(requestRepository, times(1))
                .findAllByRequesterIdOrderByCreatedDesc(anyLong());
        verifyNoMoreInteractions(requestRepository);
    }

    @Test
    void getRequestsOfUserWhenUserNotFoundAndThrow() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> requestService.getRequestsOfUser(user.getId()));

        assertThat("User id 1 does not exist.", equalTo(ex.getMessage()));
    }

    @Test
    void getById() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));

        RequestDto result = requestService.getById(1L, 1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(requestDto.getId(), result.getId());
        Assertions.assertEquals(requestDto.getDescription(), result.getDescription());
        Assertions.assertEquals(requestDto.getRequesterId(), result.getRequesterId());
        Assertions.assertEquals(requestDto.getItems(), result.getItems());

        verify(requestRepository, times(1)).findById(anyLong());
    }

    @Test
    void getByIdWhenUserNotFoundAndThrow() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException("User with id 1 was not found."));

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> requestService.getById(user.getId(), request.getId()));

        assertThat("User with id 1 was not found.", equalTo(ex.getMessage()));
    }

    @Test
    void getByIdWhenRequestNotFoundAndThrow() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(requestRepository.findById(anyLong()))
                .thenThrow(new NotFoundException("Request with id 1 was not found."));

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> requestService.getById(user.getId(), request.getId()));

        assertThat("Request with id 1 was not found.", equalTo(ex.getMessage()));
    }
}