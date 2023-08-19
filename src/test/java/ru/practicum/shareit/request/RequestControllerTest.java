package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {
    private final UserDto userDto = new UserDto(
            1L,
            "User1",
            "user1@yandex.ru");
    private final RequestDto requestDto = new RequestDto(
            1L,
            "DescriptionRequest",
            1L,
            LocalDateTime.now(),
            new ArrayList<>());
    @MockBean
    private RequestService requestService;
    @MockBean
    private UserService userService;
    @MockBean
    private RequestRepository requestRepository;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        when(userService.getById(anyLong())).thenReturn(userDto);
    }


    @Test
    void add() throws Exception {
        when(requestService.add(any(RequestDto.class), anyLong())).thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().is(201));
    }

    @Test
    void getRequestsOfUser() throws Exception {
        when(requestService.getRequestsOfUser(anyLong())).thenReturn(List.of(requestDto));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAll() throws Exception {
        when(requestService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getById() throws Exception {
        when(requestService.getById(anyLong(), anyLong())).thenReturn(requestDto);

        mvc.perform(get("/requests/{requestId}", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }
}