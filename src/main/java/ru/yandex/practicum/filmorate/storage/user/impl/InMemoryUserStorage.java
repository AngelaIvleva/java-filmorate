package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    private int createId() {
        return ++id;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        try {
            user.setId(createId());
            users.put(user.getId(), user);
            log.info("User {} is created", user.getLogin());
        } catch (ValidationException ex) {
            log.error(ex.getMessage());
            throw new ValidationException(ex);
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        try {
            if (users.containsKey(user.getId())) {
                users.replace(user.getId(), user);
                log.info("User {} is update", user.getLogin());
            } else {
                throw new NotFoundException("There is no such user");
            }
        } catch (NotFoundException ex) {
            log.error(ex.getMessage());
            throw new NotFoundException(ex.getMessage());
        }
        return user;
    }

    @Override
    public User getUserById(int id) {
        if (users.get(id) != null) {
            return users.get(id);
        } else {
            throw new NotFoundException("User ID " + id + " is not found");
        }
    }

    @Override
    public void deleteUserById(int id) {
        if (users.containsKey(id)) {
            users.remove(id);
            log.info("User ID {} is deleted", id);
        } else {
            throw new NotFoundException("User ID " + id + " is not found");
        }
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (users.containsKey(userId) && users.containsKey(friendId)) {
            users.get(userId).getFriendsList().add(friendId);
            log.info("User {} subscribed to User {}", friendId, userId);
        } else {
            throw new NotFoundException("User ID " + userId + "  or User ID " + friendId + " is not found");
        }
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        if (users.containsKey(userId) && users.containsKey(friendId)) {
            users.get(userId).getFriendsList().remove(friendId);
            log.info("User {} unsubscribed from User {}", friendId, userId);
        } else {
            throw new NotFoundException("User ID " + userId + "  or User ID " + friendId + " is not found");
        }
    }

    @Override
    public List<User> getFriendList(int id) {
        if (users.containsKey(id)) {
            return users.get(id).getFriendsList()
                    .stream()
                    .map(users::get).collect(Collectors.toList());
        } else {
            throw new NotFoundException("User ID " + id + " is not found");
        }
    }

    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        if (users.containsKey(userId) && users.containsKey(friendId)) {
            return users.get(userId).getFriendsList()
                    .stream()
                    .map(users::get)
                    .filter(u -> users.get(friendId).getFriendsList().contains(u.getId()))
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("User ID " + userId + "  or User ID " + friendId + " is not found");
        }
    }
}