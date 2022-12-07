package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTests {

    @Autowired
    private UserController userController;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void init() {
        user = new User(1L, "name", "user@email.com");
        userDto = new UserDto(1L, "name", "user@email.com");
    }

    @Test
    void createTest() {
        User userTest = userController.saveNewUser(user);
        assertEquals(userTest.getId(), userController.findById(userTest.getId()).getId());
    }

    @Test
    void updateTest() {
        userController.saveNewUser(user);
        UserDto userUpdated = new UserDto(1L, "updated name", "update@email.com");
        userController.updateUser(userUpdated, 1L);
        assertEquals(userUpdated.getEmail(), userController.findById(1L).getEmail());
        assertEquals(userUpdated.getName(), userController.findById(1L).getName());
    }

    @Test
    void updateByWrongUserTest() {
        assertThrows(NoSuchElementException.class, () -> userController.updateUser(userDto, 1L));
    }

    @Test
    void deleteTest() {
        userController.saveNewUser(user);
        assertEquals(1, userController.getAllUsers().size());
        userController.deleteUser(user.getId());
        assertEquals(0, userController.getAllUsers().size());
    }

    @Test
    void getByWrongIdTest() {
        assertThrows(NoSuchElementException.class, () -> userController.findById(1L));
    }

}