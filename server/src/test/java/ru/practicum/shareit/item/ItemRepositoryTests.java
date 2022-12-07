package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryTests {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void searchTest() {
        User user = userRepository.save(new User(1L, "name", "user@email.com"));
        itemRepository.save(new Item(1L, "name", "description", true, 1L, 1L));
        List<Item> items = itemRepository.searchItems("desc", Pageable.ofSize(10));
        assertThat(items.stream().count(), equalTo(1L));
    }

    @Test
    void findAllByOwnerIdTest() {
        User user = userRepository.save(new User(1L, "name", "user@email.com"));
        itemRepository.save(new Item(1L, "name", "description", true, 1L, 1L));
        List<Item> items = itemRepository.findItemsByOwnerIdOrderByIdAsc(user.getId(), Pageable.ofSize(10));
        assertThat(items.stream().count(), equalTo(1L));
    }

    @Test
    void findAllByRequestIdTest() {
        User user = userRepository.save(new User(1L, "name", "email@email.com"));
        User user2 = userRepository.save(new User(2L, "name2", "email2@email.com"));
        ItemRequest itemRequest = itemRequestRepository.save(new ItemRequest(1L,
                "item request desc",
                user2,
                LocalDateTime.now()));
        itemRepository.save(new Item(1L, "name", "description", true, user.getId(), itemRequest.getId()));
        assertThat(itemRepository.findAllByRequestId(itemRequest.getId()).size(), equalTo(1));
    }

    @Test
    void findAllCommentByItemIdTest() {
        User user = userRepository.save(new User(1L, "name", "email@email.com"));
        User user2 = userRepository.save(new User(2L, "name2", "email2@email.com"));
        Item item = itemRepository.save(new Item(1L, "name", "description", true, user.getId(), 1L));
        commentRepository.save(new Comment(1L, "first comment", item, user2));
        assertThat(commentRepository.findAllByItemId(item.getId()).size(), equalTo(1));
    }

}