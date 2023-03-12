package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user != null && friend != null) {
            Set<Long> list = user.getFriendsList();
            list.add(friendId);
            user.setFriendsList(list);
            Set<Long> listFr = friend.getFriendsList();
            listFr.add(userId);
            friend.setFriendsList(listFr);
            log.info("Users added as friends");
        } else {
            log.info("Users have not been added as friends");
            throw new NotFoundException("User ID " + userId + " or user ID" + friendId + "is not found");
        }
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user != null && friend != null) {
            Set<Long> list = user.getFriendsList();
            list.remove(friendId);
            user.setFriendsList(list);

            Set<Long> listFr = friend.getFriendsList();
            listFr.remove(userId);
            friend.setFriendsList(listFr);
            log.info("friend deleted");
        } else {
            log.info("friend has not been deleted");
            throw new NotFoundException("User ID " + userId + " or user ID" + friendId + "is not found");
        }
    }

    public List<User> getMutualFriends(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user.getFriendsList() != null && friend.getFriendsList() != null) {
            return user.getFriendsList().stream()
                    .map(userStorage::getUserById)
                    .filter(u -> friend.getFriendsList().contains(u.getId()))
                    .collect(Collectors.toList());
        } else {
            log.info("User ID {} or user ID {} is not found", userId, friendId);
            throw new NotFoundException("User ID " + userId + " or user ID" + friendId + "is not found");
        }
    }

    public List<User> getFriendList(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user.getFriendsList() != null) {
            return user.getFriendsList().stream()
                    .map(userStorage::getUserById)
                    .collect(Collectors.toList());
        } else {
            log.info("User ID {} is not found", userId);
            throw new NotFoundException("User ID " + userId + "is not found");
        }
    }

    public Map<Long, User> getUsers() {
        return userStorage.getUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public void deleteUserById(Long id) {
        userStorage.deleteUserById(id);
    }
}
