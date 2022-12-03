package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestRepositoryTests {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllByRequestorIdOrderByCreatedAscTest() {
        User user = userRepository.save(new User(1L, "name", "email@email.com"));
        itemRequestRepository.save(new ItemRequest(1L, "description", user, LocalDateTime.now()));
        List<ItemRequest> items = itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(user.getId(), Pageable.ofSize(10));
        assertThat(items.size(), equalTo(1));
    }

    @Test
    void findAllByRequestorNotLikeOrderByCreatedAscTest() {
        User user = userRepository.save(new User(1L, "name", "email@email.com"));
        itemRequestRepository.save(new ItemRequest(1L, "description", user, LocalDateTime.now()));
        assertThat(itemRequestRepository.findAllByRequestorNotLikeOrderByCreatedAsc(user, Pageable.ofSize(10))
                .stream().count(), equalTo(0L));
        User user2 = userRepository.save(new User(2L, "name2", "email2@email.com"));
        assertThat(itemRequestRepository.findAllByRequestorNotLikeOrderByCreatedAsc(user2, Pageable.ofSize(10))
                .stream().count(), equalTo(1L));
    }
}