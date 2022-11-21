package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDtoWithId;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemControllerTests {

    @Autowired
    private ItemController itemController;

    @Autowired
    private UserController userController;

    @Autowired
    private BookingController bookingController;

    @Autowired
    private ItemRequestController itemRequestController;

    private ItemDto itemDto;
    private User user;

    private ItemRequestDto itemRequestDto;

    private CommentDto comment;

    @BeforeEach
    void init() {
        itemDto = new ItemDto(1L, "name", "description", true, 1L, 1L);

        user = new User(1L, "name", "user@email.com");

        itemRequestDto = new ItemRequestDto(1L, "item request description", LocalDateTime.now(), new ArrayList<>());

        comment = new CommentDto(1L, "first comment", 1L, "name");
    }

    @Test
    void createItemTest() {
        User testUser = userController.saveNewUser(user);
        ItemDto item = itemController.add(1L, itemDto);
        assertEquals(item.getId(), itemController.getItem(item.getId(), testUser.getId()).getId());
    }

    @Test
    void createItemWithRequestTest() {
        User testUser = userController.saveNewUser(user);
        itemRequestController.create(testUser.getId(), itemRequestDto);
        itemDto.setRequestId(1L);
        userController.saveNewUser(new User(2L, "name", "user2@email.com"));
        ItemDto item = itemController.add(2L, itemDto);
        assertEquals(item, itemController.getItem(2L, 1L));
    }

    @Test
    void createItemByWrongUser() {
        assertThrows(NoSuchElementException.class, () -> itemController.add(1L, itemDto));
    }

    @Test
    void updateItemTest() {
        userController.saveNewUser(user);
        itemController.add(1L, itemDto);
        ItemDto item = new ItemDto(1L, "new name", "updateDescription", false, 1L, 1L);
        itemController.updateItem(1L, item, 1L);
        assertEquals(item.getDescription(), itemController.getItem(1L, 1L).getDescription());
        assertEquals(item.getName(), itemController.getItem(1L, 1L).getName());
    }

    @Test
    void updateForWrongItemTest() {
        assertThrows(NoSuchElementException.class, () -> itemController.updateItem(1L, itemDto, 1L));
    }

    @Test
    void updateByWrongUserTest() {
        userController.saveNewUser(user);
        itemController.add(1L, itemDto);
        assertThrows(NoSuchElementException.class, () -> itemController.updateItem(10L, new ItemDto(1L, "new name", "updateDescription", false, 1L, 1L), 1L));
    }

    @Test
    void deleteItemTest() {
        userController.saveNewUser(user);
        itemController.add(1L, itemDto);
        assertEquals(1, itemController.getUsersItems(1L, 0, 10).size());
        itemController.deleteItem(1L, 1L);
        assertEquals(0, itemController.getUsersItems(1L, 0, 10).size());
    }

    @Test
    void searchItemTest() {
        userController.saveNewUser(user);
        itemController.add(1L, itemDto);
        assertEquals(1, itemController.getSearchedItems(1L, "Desc", 0, 10).size());
    }

    @Test
    void searchItemEmptyTextTest() {
        userController.saveNewUser(user);
        itemController.add(1L, itemDto);
        assertEquals(new ArrayList<ItemDto>(), itemController.getSearchedItems(1L, "", 0, 10));
    }

    @Test
    void createCommentTest() throws InterruptedException {
        userController.saveNewUser(user);
        ItemDto item = itemController.add(1L, itemDto);
        User userTest2 = userController.saveNewUser(new User(2L, "name", "email2@mail.com"));
        bookingController.add(userTest2.getId(), new BookingDtoWithId(1L,
                LocalDateTime.now().plusNanos(500000000L),
                LocalDateTime.now().plusNanos(500000000L),
                1L,
                2L,
                WAITING));
        bookingController.changeStatus(1L, 1L, true);
        Thread.sleep(1000);
        itemController.addComment(userTest2.getId(), item.getId(), comment);
        assertEquals(1, itemController.getItem(1L, 1L).getComments().size());
    }

    @Test
    void createCommentByWrongUser() {
        assertThrows(NoSuchElementException.class, () -> itemController.addComment(1L, 1L, comment));
    }

    @Test
    void createCommentToWrongItem() {
        userController.saveNewUser(user);
        assertThrows(NoSuchElementException.class, () -> itemController.addComment(1L, 1L, comment));
        itemController.add(1L, itemDto);
        assertThrows(ValidationException.class, () -> itemController.addComment(1L, 1L, comment));
    }
}
