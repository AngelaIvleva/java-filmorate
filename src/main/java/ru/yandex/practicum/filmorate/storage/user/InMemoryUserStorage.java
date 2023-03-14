package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;


    private long createId() {
        return ++id;
    }

    @Override
    public Map<Long, User> getUsers() {
        return users;
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
    public User getUserById(Long id) {
        if (users.get(id) != null) {
            return users.get(id);
        } else {
            throw new NotFoundException("User ID " + id + "is not found");
        }
    }

    @Override
    public void deleteUserById(Long id) {
        if (users.containsKey(id)) {
            users.remove(id);
            log.info("User ID {} is deleted", id);
        } else {
            throw new NotFoundException("User ID " + id + "is not found");
        }
    }
}
