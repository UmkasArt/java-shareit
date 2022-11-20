package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestControllerTests {

    @Autowired
    private ItemRequestController itemRequestController;

    @Autowired
    private UserController userController;

    private ItemRequestDto itemRequestDto;

    private User user;

    @BeforeEach
    void init() {
        itemRequestDto = new ItemRequestDto(1L, "item request description", LocalDateTime.now(), new ArrayList<>());

        user = new User(1L, "name", "user@email.com");
    }

    @Test
    void createItemRequestTest() {
        User userTest = userController.saveNewUser(user);
        ItemRequestDto itemRequest = itemRequestController.create(userTest.getId(), itemRequestDto);
        assertEquals(1L, itemRequestController.getById(itemRequest.getId(), user.getId()).getId());
    }

    @Test
    void createItemRequestByWrongUserTest() {
        assertThrows(NoSuchElementException.class, () -> itemRequestController.create(1L, itemRequestDto));
    }

    @Test
    void getAllItemRequestByUserTest() {
        User userTest = userController.saveNewUser(user);
        ItemRequestDto itemRequest = itemRequestController.create(userTest.getId(), itemRequestDto);
        assertEquals(1, itemRequestController.getAllByUser(userTest.getId()).size());
    }

    @Test
    void getAllByUserWithWrongUserTest() {
        assertThrows(NoSuchElementException.class, () -> itemRequestController.getAllByUser(1L));
    }

    @Test
    void getAll() {
        User userTest = userController.saveNewUser(user);
        ItemRequestDto itemRequest = itemRequestController.create(userTest.getId(), itemRequestDto);
        assertEquals(0, itemRequestController.getAll(0, 10, user.getId()).size());
        User userTest2 = userController.saveNewUser(new User(2L, "name", "user1@email.com"));
        assertEquals(1, itemRequestController.getAll(0, 10, userTest2.getId()).size());
    }

    @Test
    void getAllByWrongUser() {
        assertThrows(NoSuchElementException.class, () -> itemRequestController.getAll(0, 10, 1L));
    }

    @Test
    void getAllWithWrongFrom() {
        assertThrows(ValidationException.class, () -> itemRequestController.getAll(-1, 10, 1L));
    }

}
