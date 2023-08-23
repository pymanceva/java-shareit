package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private final ItemDto itemDto = new ItemDto(
            1L,
            "Item1",
            "DescriptionItem1",
            true,
            1L);
    private final ItemDtoWithBookings itemDtoWithBookings = new ItemDtoWithBookings(
            1L,
            "Item1",
            "DescriptionItem1",
            true,
            1L,
            null,
            null,
            new ArrayList<>());
    private final UserDto userDto = new UserDto(
            1L,
            "User1",
            "user1@yandex.ru");
    private final CommentDto commentDto = new CommentDto(
            1L,
            "CommentText",
            "User1",
            LocalDateTime.now());
    @MockBean
    private ItemService itemService;
    @MockBean
    private UserService userService;
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
        when(itemService.add(any(ItemDto.class), anyLong())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().is(201));
    }

    @Test
    void update() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mvc.perform(patch("/items/{id}", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteById() throws Exception {
        mvc.perform(delete("/items/{id}", "1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllByUserId() throws Exception {
        when(itemService.getAllByUserId(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemDtoWithBookings));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getById() throws Exception {
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemDtoWithBookings);

        mvc.perform(get("/items/{id}", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void search() throws Exception {
        when(itemService.getAllByUserId(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemDtoWithBookings));

        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("text", "searchItem")
                        .content(mapper.writeValueAsString(itemDtoWithBookings))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void addComment() throws Exception {
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemDtoWithBookings);
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDto);

        mvc.perform(post("/items/{id}/comment", "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }
}