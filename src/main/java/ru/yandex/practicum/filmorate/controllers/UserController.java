package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @GetMapping("/users")
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/user")
    public User createUser(@Valid @RequestBody User user) {
        user.setId(createId());
        validation(user);
        users.put(user.getId(), user);
        log.debug("User {} is created", user.getLogin());
        return user;
    }

    @PutMapping(value = "/user")
    public User updateUser(@Valid @RequestBody User user) {
        try {
            if (users.containsKey(user.getId())) {
                validation(user);
                users.replace(user.getId(), user);
                log.debug("User {} is update", user.getLogin());
            } else {
                throw new ValidationException("There is no such user");
            }
        } catch (ValidationException ex) {
            log.warn(ex.getMessage());
            throw new ValidationException(ex);
        }
        return user;
    }

    private int createId() {
        return id++;
    }

    public void validation(User user)  {
        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
