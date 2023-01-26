package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.PageableMaker;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private Pageable pageable;
    private User firstUser;
    private User secondUser;
    private Item firstItem;
    private Item secondItem;
    private Item thirdItem;
    private ItemRequest firstRequest;
    private ItemRequest secondRequest;

    @BeforeEach
    public void beforeEach() {
        firstUser = userRepository.save(new User(1L, "Имя первого", "first@email.com"));
        testEntityManager.persist(firstUser);
        secondUser = userRepository.save(new User(2L, "Имя второго", "second@email.com"));
        testEntityManager.persist(secondUser);
        firstRequest = itemRequestRepository.save(new ItemRequest(1L, "Реквест на первый и второй", firstUser, LocalDateTime.now()));
        testEntityManager.persist(firstRequest);
        secondRequest = itemRequestRepository.save(new ItemRequest(2L, "Реквест на третий", firstUser, LocalDateTime.now()));
        testEntityManager.persist(secondRequest);
        firstItem = itemRepository.save(new Item(1L, "Название первого", "Описание первого", true, 1L, 1L));
        testEntityManager.persist(firstItem);
        secondItem = itemRepository.save(new Item(2L, "Название второго", "Описание второго", true, 1L, 1L));
        testEntityManager.persist(secondItem);
        thirdItem = itemRepository.save(new Item(3L, "Название третьего", "Описание третьего", true, 2L, 2L));
        testEntityManager.persist(thirdItem);
        testEntityManager.getEntityManager().getTransaction().commit();
        pageable = PageableMaker.makePage(0, 10);
    }

    @AfterEach
    public void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    public void findAllByOwnerTest() {
        List<Item> items = itemRepository.findAllByOwner(1L, pageable);

        assertEquals(2, items.size());
    }

    @Test
    public void findAllByRequestIdTest() {
        List<Item> items = itemRepository.findAllByRequestId(1L);

        assertEquals(2, items.size());
    }

    @Test
    public void findByText() {
        List<Item> items = itemRepository.findByText("вТоРоГо", pageable);

        assertEquals(1, items.size());
    }
}
