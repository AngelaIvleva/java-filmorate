package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    Collection<User> getUsers();

    User createUser(User user);

    User updateUser(User user);

    User getUserById(int id);

    void deleteUserById(int id);

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getFriendList(int id);

    List<User> getCommonFriends(int userId, int friendId);

}
