package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.booking.model.BookingStatus.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryTests {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;

    private Item item;

    private User user2;

    private Booking booking;

    @BeforeEach
    void init() {
        user = new User(1L, "name", "email@email.com");

        item = new Item(1L, "name", "description", true, user.getId(), 1L);

        user2 = new User(2L, "name2", "email2@email.com");

        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusMonths(1L), item, user2, WAITING);
    }

    @Test
    void findAllByItemIdOrderByStartAscTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByItemIdAndBooker_IdNotAndStatusNot(item.getId(), user.getId(), REJECTED).size(), equalTo(1));
    }

    @Test
    void findAllByBookerTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByBookerOrderByStartDesc(user2, Pageable.ofSize(10)).stream().count(), equalTo(1L));
    }

    @Test
    void findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBeforeTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        booking.setStatus(APPROVED);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByItemIdAndBooker_IdAndStatusAndEndIsBefore(item.getId(),
                        user2.getId(), APPROVED, LocalDateTime.of(2023, 3, 10, 10, 10)).size(),
                equalTo(1));
    }

    @Test
    void findAllByBookerAndStartBeforeAndEndAfterTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user2,
                LocalDateTime.of(2023, 2, 1, 10, 10), LocalDateTime.now(),
                Pageable.ofSize(10)).stream().count(), equalTo(1L));
    }

    @Test
    void findAllByItemOwnerAndEndBeforeTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByItem_OwnerIdAndEndIsBeforeOrderByStartDesc(user.getId(),
                LocalDateTime.of(2023, 4, 10, 10, 10),
                Pageable.ofSize(10)).stream().count(), equalTo(1L));
    }

    @Test
    void findAllByItemOwnerAndStartAfterTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByItem_OwnerIdAndStartIsAfterOrderByStartDesc(user.getId(),
                LocalDateTime.now().minusDays(1L), Pageable.ofSize(10)).stream().count(), equalTo(1L));
    }

    @Test
    void findAllByItemOwnerAndStatusEqualsTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(user.getId(), WAITING, Pageable.ofSize(10))
                .stream().count(), equalTo(1L));
    }
}