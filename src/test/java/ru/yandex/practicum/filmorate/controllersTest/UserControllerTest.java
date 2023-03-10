package ru.yandex.practicum.filmorate.controllersTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    UserController userController;

    @BeforeEach
    void beforeEach() {
        userController = new UserController();
    }

    @Test
    void shouldCreateUser() {
        User user = User.builder()
                .email("columbus1958@gmail.com")
                .login("columbus1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        assertEquals("Christopher Joseph Columbus", userController.createUser(user).getName());

        User user1 = User.builder()
                .email("dfghjkl@gmail.com")
                .login("hgfd")
                .name("dfghjkl;")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        assertEquals(2, userController.createUser(user1).getId());
        assertEquals(2, userController.getUsers().size());
        assertTrue(userController.getUsers().containsKey(2));
    }

    @Test
    void shouldNotCreateUserWhenEmailIsEmpty() {
        User user = User.builder()
                .email(" ")
                .login("columbus1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        assertThrows(ValidationException.class,  () -> userController.createUser(user));
    }

    @Test
    void shouldNotCreateUserWhenEmailIsWrong() {
        User user = User.builder()
                .email("columbus1958gmail.com")
                .login("columbus1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        assertThrows(ValidationException.class,  () -> userController.createUser(user));
    }

    @Test
    void shouldNotCreateUserWhenLoginIsEmpty() {
        User user = User.builder()
                .email("columbus1958@gmail.com")
                .login("")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        assertThrows(ValidationException.class,  () -> userController.createUser(user));
    }

    @Test
    void shouldNotCreateUserWhenLoginIsWrong() {
        User user = User.builder()
                .email("columbus1958@gmail.com")
                .login("columbus 1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        assertThrows(ValidationException.class,  () -> userController.createUser(user));
    }

    @Test
    void shouldCreateUserWhenNameIsEmpty() {
        User user = User.builder()
                .email("columbus1958@gmail.com")
                .login("columbus1958")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        assertEquals("columbus1958", userController.createUser(user).getName());
    }

    @Test
    void shouldNotCreateUserWhenDateOfBirthInTheFuture() {
        User user = User.builder()
                .email("columbus1958@gmail.com")
                .login("columbus1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.now().plusDays(1))
                .build();
        assertThrows(ValidationException.class,  () -> userController.createUser(user));
    }

    @Test
    void shouldUpdateUser() {
        User user = User.builder()
                .email("columbus1958@gmail.com")
                .login("columbus1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        assertEquals("Christopher Joseph Columbus", userController.createUser(user).getName());
        user.setName("C.J.Columbus");
        assertEquals("C.J.Columbus", userController.updateUser(user).getName());
    }
    @Test
    void shouldNotUpdateUserWhenUserIsNotFound() {
        User user = User.builder()
                .email("columbus1958@gmail.com")
                .login("columbus1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        assertThrows(ValidationException.class,  () -> userController.updateUser(user));
    }
}
