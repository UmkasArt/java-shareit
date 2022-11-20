package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithId;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingControllerTests {
    @Autowired
    private BookingController bookingController;

    @Autowired
    private UserController userController;

    @Autowired
    private ItemController itemController;

    private ItemDto itemDto;

    private User user;

    private User user1;

    private BookingDtoWithId bookingShortDto;

    @BeforeEach
    void init() {
        itemDto = new ItemDto(1L, "name", "description", true, 1L, 1L);

        user = new User(1L, "name", "user@email.com");

        user1 = new User(2L, "name", "user1@email.com");

        bookingShortDto = new BookingDtoWithId(1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusYears(1),
                1L,
                2L,
                WAITING);
    }

    @Test
    void createBookingTest() throws InterruptedException {
        User userTest = userController.saveNewUser(user);
        ItemDto item = itemController.add(userTest.getId(), itemDto);
        User userTest1 = userController.saveNewUser(user1);
        BookingDto booking = bookingController.add(userTest1.getId(), bookingShortDto);
        Thread.sleep(1000);
        assertEquals(1L, bookingController.getByUserIdAndBookingId(userTest1.getId(), booking.getId()).getId());
    }

    @Test
    void createBookingByWrongUserTest() {
        assertThrows(NoSuchElementException.class, () -> bookingController.add(1L, bookingShortDto));
    }

    @Test
    void createBookingForWrongItemTest() {
        User userTest = userController.saveNewUser(user);
        assertThrows(NoSuchElementException.class, () -> bookingController.add(1L, bookingShortDto));
    }

    @Test
    void createBookingByOwnerTest() {
        User userTest = userController.saveNewUser(user);
        ItemDto item = itemController.add(userTest.getId(), itemDto);
        assertThrows(NoSuchElementException.class, () -> bookingController.add(1L, bookingShortDto));
    }

    @Test
    void createBookingToUnavailableItemTest() {
        User userTest = userController.saveNewUser(user);
        itemDto.setAvailable(false);
        ItemDto item = itemController.add(userTest.getId(), itemDto);
        User userTest1 = userController.saveNewUser(user1);
        assertThrows(ValidationException.class, () -> bookingController.add(2L, bookingShortDto));
    }

    @Test
    void createBookingWithWrongEndDate() {
        User userTest = userController.saveNewUser(user);
        ItemDto item = itemController.add(userTest.getId(), itemDto);
        User userTest1 = userController.saveNewUser(user1);
        bookingShortDto.setEnd(LocalDateTime.now().minusMonths(1L));
        assertThrows(ValidationException.class, () -> bookingController.add(userTest1.getId(), bookingShortDto));
    }

    @Test
    void approveBookingTest() {
        User userTest = userController.saveNewUser(user);
        ItemDto item = itemController.add(userTest.getId(), itemDto);
        User userTest1 = userController.saveNewUser(user1);
        BookingDto booking = bookingController.add(userTest1.getId(), new BookingDtoWithId(2L,
                LocalDateTime.now().plusNanos(500000000L),
                LocalDateTime.now().plusNanos(600000000L),
                1L,
                2L,
                WAITING));
        assertEquals(WAITING, bookingController.getByUserIdAndBookingId(userTest1.getId(), booking.getId()).getStatus());
        bookingController.changeStatus(userTest.getId(), booking.getId(), true);
        assertEquals(APPROVED, bookingController.getByUserIdAndBookingId(userTest1.getId(), booking.getId()).getStatus());
    }

    @Test
    void approveBookingToWrongTest() {
        assertThrows(NoSuchElementException.class, () -> bookingController.changeStatus(1L, 1L, true));
    }

    @Test
    void approveBookingByWrongUserTest() {
        User userTest = userController.saveNewUser(user);
        ItemDto item = itemController.add(userTest.getId(), itemDto);
        User userTest1 = userController.saveNewUser(user1);
        BookingDto booking = bookingController.add(userTest1.getId(), bookingShortDto);
        assertThrows(NoSuchElementException.class, () -> bookingController.changeStatus(2L, 1L, true));
    }

    @Test
    void approveToBookingWithWrongStatus() {
        User userTest = userController.saveNewUser(user);
        ItemDto item = itemController.add(userTest.getId(), itemDto);
        User userTest1 = userController.saveNewUser(user1);
        BookingDto booking = bookingController.add(userTest1.getId(), bookingShortDto);
        bookingController.changeStatus(1L, 1L, true);
        assertThrows(ValidationException.class, () -> bookingController.changeStatus(1L, 1L, true));
    }

    @Test
    void getAllBookingByUserTest() {
        User userTest = userController.saveNewUser(user);
        ItemDto item = itemController.add(userTest.getId(), itemDto);
        User userTest1 = userController.saveNewUser(user1);
        BookingDto booking = bookingController.add(userTest1.getId(), bookingShortDto);
        assertEquals(1, bookingController.getAllByUser(userTest1.getId(), "WAITING", 0, 10).size());
        assertEquals(1, bookingController.getAllByUser(userTest1.getId(), "ALL", 0, 10).size());
        assertEquals(0, bookingController.getAllByUser(userTest1.getId(), "PAST", 0, 10).size());
        assertEquals(0, bookingController.getAllByUser(userTest1.getId(), "CURRENT", 0, 10).size());
        assertEquals(1, bookingController.getAllByUser(userTest1.getId(), "FUTURE", 0, 10).size());
        assertEquals(0, bookingController.getAllByUser(userTest1.getId(), "REJECTED", 0, 10).size());
        bookingController.changeStatus(userTest.getId(), booking.getId(), true);
        assertEquals(0, bookingController.findAllByOwner(userTest.getId(), "CURRENT", 0, 10).size());
        assertEquals(1, bookingController.findAllByOwner(userTest.getId(), "ALL", 0, 10).size());
        assertEquals(0, bookingController.findAllByOwner(userTest.getId(), "WAITING", 0, 10).size());
        assertEquals(1, bookingController.findAllByOwner(userTest.getId(), "FUTURE", 0, 10).size());
        assertEquals(0, bookingController.findAllByOwner(userTest.getId(), "REJECTED", 0, 10).size());
        assertEquals(0, bookingController.findAllByOwner(userTest.getId(), "PAST", 0, 10).size());
    }

    @Test
    void getAllBookingByWrongUserTest() {
        assertThrows(NoSuchElementException.class, () -> bookingController.getAllByUser(1L, "ALL", 0, 10));
        assertThrows(NoSuchElementException.class, () -> bookingController.findAllByOwner(1L, "ALL", 0, 10));
    }

    @Test
    void getByBookingWrongIdTest() {
        assertThrows(NoSuchElementException.class, () -> bookingController.getByUserIdAndBookingId(1L, 1L));
    }

    @Test
    void getByWrongUser() {
        User userTest = userController.saveNewUser(user);
        ItemDto item = itemController.add(userTest.getId(), itemDto);
        User userTest1 = userController.saveNewUser(user1);
        BookingDto booking = bookingController.add(userTest1.getId(), bookingShortDto);
        assertThrows(NoSuchElementException.class, () -> bookingController.getByUserIdAndBookingId(10L, 1L));
    }
}
