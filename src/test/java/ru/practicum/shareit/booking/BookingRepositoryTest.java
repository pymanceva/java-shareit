package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {
    private final User user = new User(
            1L,
            "User1",
            "user1@yandex.ru");
    private final User booker = new User(
            2L,
            "User2",
            "user2@yandex.ru");
    private final User requester = new User(
            3L,
            "User3",
            "user3@yandex.ru");
    private final Request request = new Request(
            1L,
            "Description",
            requester,
            LocalDateTime.of(2022, 12, 1, 0, 0, 0),
            new ArrayList<>());
    private final Item item = new Item(
            1L,
            "Item1",
            "DescriptionItem1",
            true,
            user,
            1L);
    private final Booking booking = new Booking(
            1L,
            LocalDateTime.of(2023, 1, 1, 0, 0, 0),
            LocalDateTime.of(2023, 1, 1, 1, 1, 1),
            item,
            booker,
            BookingStatus.WAITING
    );

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RequestRepository requestRepository;

    @BeforeEach
    void setup() {
        userRepository.save(user);
        userRepository.save(booker);
        userRepository.save(requester);
        requestRepository.save(request);
        itemRepository.save(item);
        bookingRepository.save(booking);
    }

    @AfterEach
    void deleteAll() {
        requestRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByBookerIdOrderByEndDesc() {
        List<Booking> found = bookingRepository.findAllByBookerIdOrderByEndDesc(2L, PageRequest.of(0, 10));

        Assertions.assertNotNull(found);
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals(1L, found.get(0).getId());
        Assertions.assertEquals(booking.getStart(), found.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), found.get(0).getEnd());
        Assertions.assertEquals(booking.getItem(), found.get(0).getItem());
        Assertions.assertEquals(booking.getBooker().getName(), found.get(0).getBooker().getName());
        Assertions.assertEquals(booking.getStatus(), found.get(0).getStatus());
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc() {
        List<Booking> found = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(
                2L,
                LocalDateTime.of(2023, 1, 1, 0, 5, 0),
                LocalDateTime.of(2023, 1, 1, 0, 10, 0),
                PageRequest.of(0, 10));

        Assertions.assertNotNull(found);
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals(1L, found.get(0).getId());
        Assertions.assertEquals(booking.getStart(), found.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), found.get(0).getEnd());
        Assertions.assertEquals(booking.getItem(), found.get(0).getItem());
        Assertions.assertEquals(booking.getBooker().getName(), found.get(0).getBooker().getName());
        Assertions.assertEquals(booking.getStatus(), found.get(0).getStatus());
    }

    @Test
    void findAllByBookerIdAndEndBeforeOrderByStartDesc() {
        List<Booking> found = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                2L,
                LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                PageRequest.of(0, 10));

        Assertions.assertNotNull(found);
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals(1L, found.get(0).getId());
        Assertions.assertEquals(booking.getStart(), found.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), found.get(0).getEnd());
        Assertions.assertEquals(booking.getItem(), found.get(0).getItem());
        Assertions.assertEquals(booking.getBooker().getName(), found.get(0).getBooker().getName());
        Assertions.assertEquals(booking.getStatus(), found.get(0).getStatus());
    }

    @Test
    void findAllByBookerIdAndStartAfterOrderByStartDesc() {
        List<Booking> found = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                2L,
                LocalDateTime.of(2022, 1, 1, 0, 0, 0),
                PageRequest.of(0, 10));

        Assertions.assertNotNull(found);
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals(1L, found.get(0).getId());
        Assertions.assertEquals(booking.getStart(), found.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), found.get(0).getEnd());
        Assertions.assertEquals(booking.getItem(), found.get(0).getItem());
        Assertions.assertEquals(booking.getBooker().getName(), found.get(0).getBooker().getName());
        Assertions.assertEquals(booking.getStatus(), found.get(0).getStatus());
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc() {
        List<Booking> found = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                2L,
                BookingStatus.WAITING,
                PageRequest.of(0, 10));

        Assertions.assertNotNull(found);
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals(1L, found.get(0).getId());
        Assertions.assertEquals(booking.getStart(), found.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), found.get(0).getEnd());
        Assertions.assertEquals(booking.getItem(), found.get(0).getItem());
        Assertions.assertEquals(booking.getBooker().getName(), found.get(0).getBooker().getName());
        Assertions.assertEquals(booking.getStatus(), found.get(0).getStatus());
    }
}