package ru.yandex.practicum.filmorate.controllersTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    UserController userController;

    @BeforeEach
    void beforeEach() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
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
        assertEquals(2, userController.findAllUsers().size());
    }

    @Test
    void shouldNotCreateUserWhenEmailIsEmpty() {
        User user = User.builder()
                .email(" ")
                .login("columbus1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldNotCreateUserWhenEmailIsWrong() {
        User user = User.builder()
                .email("columbus1958gmail.com")
                .login("columbus1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldNotCreateUserWhenLoginIsEmpty() {
        User user = User.builder()
                .email("columbus1958@gmail.com")
                .login("")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void shouldNotCreateUserWhenLoginIsWrong() {
        User user = User.builder()
                .email("columbus1958@gmail.com")
                .login("columbus 1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        assertThrows(ValidationException.class, () -> userController.createUser(user));
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
        assertThrows(ValidationException.class, () -> userController.createUser(user));
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
        userController.createUser(user);
        User user1 = User.builder()
                .id(2222)
                .email("columbus1958@gmail.com")
                .login("columbus1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        assertThrows(NotFoundException.class, () -> userController.updateUser(user1));
    }

    @Test
    void shouldAddFriend() {
        User user = User.builder()
                .email("columbus1958@gmail.com")
                .login("columbus1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        userController.createUser(user);
        User friend = User.builder()
                .email("friend@gmail.com")
                .login("friend")
                .name("friendName")
                .birthday(LocalDate.of(1977, 12, 10))
                .build();
        userController.createUser(friend);
        userController.addFriend(user.getId(), friend.getId());

        assertEquals(1, user.getFriendsList().size());
        assertEquals(1, friend.getFriendsList().size());
    }

    @Test
    void shouldNotAddFriendWhenWrongAnyID() {
        User user = User.builder()
                .email("columbus1958@gmail.com")
                .login("columbus1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();

        User friend = User.builder()
                .email("friend@gmail.com")
                .login("friend")
                .name("friendName")
                .birthday(LocalDate.of(1977, 12, 10))
                .build();
        userController.createUser(friend);
        assertThrows(NotFoundException.class, () -> userController.addFriend(user.getId(), friend.getId()));
    }

    @Test
    void shouldDeleteFriend() {
        User user = User.builder()
                .email("columbus1958@gmail.com")
                .login("columbus1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        userController.createUser(user);
        User friend = User.builder()
                .email("friend@gmail.com")
                .login("friend")
                .name("friendName")
                .birthday(LocalDate.of(1977, 12, 10))
                .build();
        userController.createUser(friend);
        userController.addFriend(user.getId(), friend.getId());
        assertEquals(1, user.getFriendsList().size());

        userController.deleteFriend(user.getId(), friend.getId());
        assertEquals(0, user.getFriendsList().size());
        assertEquals(0, friend.getFriendsList().size());
    }

    @Test
    void shouldNotDeleteFriendWhenWrongAnyID() {
        User user = User.builder()
                .email("columbus1958@gmail.com")
                .login("columbus1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        userController.createUser(user);
        User friend = User.builder()
                .email("friend@gmail.com")
                .login("friend")
                .name("friendName")
                .birthday(LocalDate.of(1977, 12, 10))
                .build();
        userController.createUser(friend);
        userController.addFriend(user.getId(), friend.getId());
        assertEquals(1, user.getFriendsList().size());

        assertThrows(NotFoundException.class, () -> userController.deleteFriend(user.getId(), 3L));
        assertEquals(1, user.getFriendsList().size());
        assertEquals(1, friend.getFriendsList().size());
    }

    @Test
    void shouldDeleteUserByID() {
        User user = User.builder()
                .email("columbus1958@gmail.com")
                .login("columbus1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        userController.createUser(user);
        assertEquals(1, user.getId());

        userController.deleteUserById(user.getId());
        assertThrows(NotFoundException.class, () -> userController.getUserById(user.getId()));
    }

    @Test
    void shouldNotDeleteUserWhenWrongID() {
        User user = User.builder()
                .email("columbus1958@gmail.com")
                .login("columbus1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        userController.createUser(user);
        assertEquals(1, user.getId());
        assertThrows(NotFoundException.class, () -> userController.deleteUserById(3L));
    }

    @Test
    void shouldGetMutualFriendsList() {
        User user = User.builder()
                .email("columbus1958@gmail.com")
                .login("columbus1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        userController.createUser(user);
        User friend = User.builder()
                .email("friend@gmail.com")
                .login("friend")
                .name("friendName")
                .birthday(LocalDate.of(1977, 12, 10))
                .build();
        userController.createUser(friend);
        User friend1 = User.builder()
                .email("friend@gmail.com")
                .login("friend1")
                .name("friendName")
                .birthday(LocalDate.of(1977, 12, 10))
                .build();
        userController.createUser(friend1);
        userController.addFriend(user.getId(), friend.getId());
        userController.addFriend(user.getId(), friend1.getId());

        assertEquals(2, user.getFriendsList().size());
        assertEquals(1, friend.getFriendsList().size());
        assertEquals(1, friend1.getFriendsList().size());
        assertEquals(1, userController.getCommonFriends(friend1.getId(), friend.getId()).size());
    }

    @Test
    void shouldNotGetMutualFriendsListWhenWrongAnyID() {
        User user = User.builder()
                .email("columbus1958@gmail.com")
                .login("columbus1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        userController.createUser(user);
        User friend = User.builder()
                .email("friend@gmail.com")
                .login("friend")
                .name("friendName")
                .birthday(LocalDate.of(1977, 12, 10))
                .build();
        userController.createUser(friend);
        User friend1 = User.builder()
                .email("friend@gmail.com")
                .login("friend1")
                .name("friendName")
                .birthday(LocalDate.of(1977, 12, 10))
                .build();
        userController.createUser(friend1);
        userController.addFriend(user.getId(), friend.getId());
        userController.addFriend(user.getId(), friend1.getId());

        assertEquals(2, user.getFriendsList().size());
        assertEquals(1, friend.getFriendsList().size());
        assertEquals(1, friend1.getFriendsList().size());
        assertThrows(NotFoundException.class, () -> userController.getCommonFriends(user.getId(), 6666L));
    }

    @Test
    void shouldNotGetFriendsListWhenWrongID() {
        User user = User.builder()
                .email("columbus1958@gmail.com")
                .login("columbus1958")
                .name("Christopher Joseph Columbus")
                .birthday(LocalDate.of(1958, 9, 10))
                .build();
        userController.createUser(user);
        User friend = User.builder()
                .email("friend@gmail.com")
                .login("friend")
                .name("friendName")
                .birthday(LocalDate.of(1977, 12, 10))
                .build();
        userController.createUser(friend);
        userController.addFriend(user.getId(), friend.getId());

        assertEquals(1, user.getFriendsList().size());
        assertEquals(1, friend.getFriendsList().size());
        assertThrows(NotFoundException.class, () -> userController.getFriendList(33L));

    }
}
