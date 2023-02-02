package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.PageableMaker;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;
    private ItemRequest firstRequest;
    private ItemRequest secondRequest;

    @BeforeEach
    public void beforeEach() {
        user = userRepository.save(new User(1L, "Имя", "email@email.com"));
        entityManager.persist(user);
        firstRequest = itemRequestRepository.save(new ItemRequest(1L, "Описание первого", user, LocalDateTime.now()));
        entityManager.persist(firstRequest);
        secondRequest = itemRequestRepository.save(new ItemRequest(2L, "Описание второго", user, LocalDateTime.now()));
        entityManager.persist(secondRequest);
        entityManager.getEntityManager().getTransaction().commit();
    }

    @AfterEach
    public void afterEach() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void findAllItemRequestsByRequesterIdTest() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(1L);

        assertEquals(2, requests.size());
    }

    @Test
    public void findAllByRequesterIdNotTest() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(1L, PageableMaker.makePage(0, 10));

        assertEquals(0, requests.size());
    }
}
