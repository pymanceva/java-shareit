package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingOutgoingDto;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
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

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
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
    private final BookingOutgoingDto bookingOutgoingDto = new BookingOutgoingDto(
            1L,
            LocalDateTime.now().plusMinutes(1),
            LocalDateTime.now().plusHours(1),
            itemDto,
            userDto,
            BookingStatus.WAITING);
    @MockBean
    private BookingService bookingService;
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
        when(userService.getById(anyLong())).thenReturn(userDto);
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemDtoWithBookings);
        when(bookingService.add(any(BookingIncomingDto.class), anyLong()))
                .thenReturn(bookingOutgoingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingOutgoingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().is(201));
    }

    @Test
    void approve() throws Exception {
        when(bookingService.approve(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingOutgoingDto);

        mvc.perform(patch("/bookings/{bookingId}", "1")
                        .content(mapper.writeValueAsString(bookingOutgoingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void getById() throws Exception {
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingOutgoingDto);

        mvc.perform(get("/bookings/{bookingId}", "1")
                        .content(mapper.writeValueAsString(bookingOutgoingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllByBookerId() throws Exception {
        when(userService.getById(anyLong())).thenReturn(userDto);
        when(bookingService.getAllByBookerId(anyLong(), any(BookingRequestState.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutgoingDto));

        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(bookingOutgoingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllByItemsOfUser() throws Exception {
        when(userService.getById(anyLong())).thenReturn(userDto);
        when(bookingService.getAllByItemsOfUser(anyLong(), any(BookingRequestState.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutgoingDto));

        mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(bookingOutgoingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }
}