package ru.practicum.shareit.comment;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.comment.dao.CommentRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User firstUser;
    private User secondUser;
    private Item item;
    private Comment firstComment;
    private Comment secondComment;

    @BeforeEach
    public void beforeEach() {
        firstUser = userRepository.save(new User(1L, "Имя первого", "first@email.com"));
        entityManager.persist(firstUser);
        secondUser = userRepository.save(new User(2L, "Имя второго", "second@email.com"));
        entityManager.persist(secondUser);
        item = itemRepository.save(new Item(1L, "Название", "Описание", true, 1L, null));
        entityManager.persist(item);
        firstComment = commentRepository.save(new Comment(1L, "Текст первого", firstUser, 1L, LocalDateTime.now()));
        entityManager.persist(firstComment);
        secondComment = commentRepository.save(new Comment(2L, "Текст второго", secondUser, 1L, LocalDateTime.now()));
        entityManager.persist(secondComment);
        entityManager.getEntityManager().getTransaction().commit();
    }

    @AfterEach
    public void afterEach() {
        commentRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    public void findAllByItemIdTest() {
        List<Comment> comments = commentRepository.findAllByItemId(1L);

        assertEquals(2, comments.size());
    }
}
