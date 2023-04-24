package ru.yandex.practicum.filmorate.controllersTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {

    private final UserController userController;

    User user = User.builder()
            .email("columbus1958@gmail.com")
            .login("columbus1958")
            .name("Christopher Joseph Columbus")
            .birthday(LocalDate.of(1958, 9, 10))
            .build();

    @Test
    void shouldCreateUser() {
        assertThat(userController.createUser(user).getId())
                .isEqualTo(1);
    }

    @Test
    void shouldUpdateUser() {
        assertThat(userController.createUser(user).getName())
                .isEqualTo("Christopher Joseph Columbus");
        user.setName("C.J.Columbus");
        assertThat(userController.createUser(user).getName())
                .isEqualTo("C.J.Columbus");
    }

    @Test
    void shouldNotUpdateUserWhenUserIsNotFound() {
        assertThrows(NotFoundException.class, () -> userController.updateUser(user));
    }

    @Test
    void shouldAddFriend() {
        User friend = User.builder()
                .email("friend@gmail.com")
                .login("friend")
                .name("friendName")
                .birthday(LocalDate.of(1977, 12, 10))
                .build();
        userController.createUser(user);
        userController.createUser(friend);
        userController.addFriend(user.getId(), friend.getId());

        assertThat(userController.getFriendList(user.getId()).size())
                .isEqualTo(1);
        assertThat(userController.getFriendList(friend.getId()).size())
                .isEqualTo(0);
    }

    @Test
    void shouldNotAddFriendWhenWrongAnyID() {
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
        User friend = User.builder()
                .email("friend@gmail.com")
                .login("friend")
                .name("friendName")
                .birthday(LocalDate.of(1977, 12, 10))
                .build();
        userController.createUser(user);
        userController.createUser(friend);
        userController.addFriend(user.getId(), friend.getId());
        assertThat(userController.getFriendList(user.getId()).size())
                .isEqualTo(1);

        userController.deleteFriend(user.getId(), friend.getId());
        assertThat(userController.getFriendList(user.getId()).size())
                .isEqualTo(0);
    }


    @Test
    void shouldDeleteUserByID() {
        userController.createUser(user);
        assertThat(userController.getUserById(user.getId()).getId())
                .isEqualTo(1);

        userController.deleteUserById(user.getId());
        assertThrows(NotFoundException.class, () -> userController.getUserById(user.getId()));
    }

    @Test
    void shouldNotDeleteUserWhenWrongID() {
        userController.createUser(user);
        assertThat(userController.getUserById(user.getId()).getId())
                .isEqualTo(1);
        assertThrows(NotFoundException.class, () -> userController.deleteUserById(3));
    }

    @Test
    void shouldGetCommonFriendsList() {
        User friend = User.builder()
                .email("friend@gmail.com")
                .login("friend")
                .name("friendName")
                .birthday(LocalDate.of(1977, 12, 10))
                .build();
        User friend1 = User.builder()
                .email("friend@gmail.com")
                .login("friend1")
                .name("friendName")
                .birthday(LocalDate.of(1977, 12, 10))
                .build();
        userController.createUser(user);
        userController.createUser(friend);
        userController.createUser(friend1);

        userController.addFriend(user.getId(), friend.getId());
        userController.addFriend(user.getId(), friend1.getId());

        assertThat(userController.getFriendList(user.getId()).size())
                .isEqualTo(2);
        assertThat(userController.getFriendList(friend.getId()).size())
                .isEqualTo(0);
        assertThat(userController.getFriendList(friend1.getId()).size())
                .isEqualTo(0);
    }

    @Test
    void shouldNotGetCommonFriendsWhenWrongAnyID() {
        User friend = User.builder()
                .email("friend@gmail.com")
                .login("friend")
                .name("friendName")
                .birthday(LocalDate.of(1977, 12, 10))
                .build();
        User friend1 = User.builder()
                .email("friend@gmail.com")
                .login("friend1")
                .name("friendName")
                .birthday(LocalDate.of(1977, 12, 10))
                .build();
        userController.createUser(user);
        userController.createUser(friend);
        userController.createUser(friend1);

        userController.addFriend(user.getId(), friend.getId());
        userController.addFriend(user.getId(), friend1.getId());

        assertThat(userController.getFriendList(user.getId()).size())
                .isEqualTo(2);
        assertThat(userController.getFriendList(friend.getId()).size())
                .isEqualTo(0);
        assertThat(userController.getFriendList(friend1.getId()).size())
                .isEqualTo(0);
        assertThrows(NotFoundException.class, () -> userController.getCommonFriends(user.getId(), 6666));
    }

    @Test
    void shouldNotGetFriendsWhenWrongID() {
        User friend = User.builder()
                .email("friend@gmail.com")
                .login("friend")
                .name("friendName")
                .birthday(LocalDate.of(1977, 12, 10))
                .build();
        userController.createUser(user);
        userController.createUser(friend);
        userController.addFriend(user.getId(), friend.getId());

        assertThat(userController.getFriendList(user.getId()).size())
                .isEqualTo(1);
        assertThat(userController.getFriendList(friend.getId()).size())
                .isEqualTo(0);
        assertThrows(NotFoundException.class, () -> userController.getFriendList(33));

    }
}
