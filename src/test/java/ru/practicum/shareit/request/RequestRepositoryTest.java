package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
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
class RequestRepositoryTest {
    private final User user = new User(
            1L,
            "User1",
            "user1@yandex.ru");
    private final User requester = new User(
            2L,
            "User2",
            "user2@yandex.ru");
    private final Item item = new Item(
            1L,
            "Item1",
            "DescriptionItem1",
            true,
            user,
            1L);
    private final Request request = new Request(
            1L,
            "Description",
            requester,
            LocalDateTime.of(2023, 1, 1, 0, 0, 0),
            new ArrayList<>());

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;

    @BeforeEach
    void setup() {
        userRepository.save(user);
        userRepository.save(requester);
        requestRepository.save(request);
        itemRepository.save(item);
    }

    @AfterEach
    void deleteAll() {
        requestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByRequesterIdNotOrderByCreatedDesc() {
        List<Request> found = requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(1L, PageRequest.of(0, 10));

        Assertions.assertNotNull(found);
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals(1L, found.get(0).getId());
        Assertions.assertEquals(request.getDescription(), found.get(0).getDescription());
        Assertions.assertEquals(request.getCreated(), found.get(0).getCreated());
        Assertions.assertEquals(request.getRequester().getName(), found.get(0).getRequester().getName());
    }

    @Test
    void findAllByRequesterIdOrderByCreatedDesc() {
        List<Request> found = requestRepository.findAllByRequesterIdOrderByCreatedDesc(2L);

        Assertions.assertNotNull(found);
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals(1L, found.get(0).getId());
        Assertions.assertEquals(request.getDescription(), found.get(0).getDescription());
        Assertions.assertEquals(request.getCreated(), found.get(0).getCreated());
        Assertions.assertEquals(request.getRequester().getName(), found.get(0).getRequester().getName());
    }
}