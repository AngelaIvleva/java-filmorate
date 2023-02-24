package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    public Map<Integer, User> getUsers() {
        return users;
    }

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @GetMapping
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        try {
            validation(user);
            user.setId(createId());
            users.put(user.getId(), user);
            log.info("User {} is created", user.getLogin());
        } catch (ValidationException ex) {
            log.error(ex.getMessage());
            throw new ValidationException(ex);
        }
        return user;
    }

    @PutMapping
    public User updateUser( @RequestBody User user) {
        try {
            if (users.containsKey(user.getId())) {
                validation(user);
                users.replace(user.getId(), user);
                log.info("User {} is update", user.getLogin());
            } else {
                throw new ValidationException("There is no such user");
            }
        } catch (ValidationException ex) {
            log.error(ex.getMessage());
            throw new ValidationException(ex);
        }
        return user;
    }

    private int createId() {
        return ++id;
    }

    public void validation(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException("User cannot be null");
        }
        if (user.getName() == null || user.getName().isBlank() ) {
            log.info("Name is empty. Login is set as Name");
            user.setName(user.getLogin());
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Email cannot be empty");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Email doesn't contain @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ") ) {
            throw new ValidationException("Login cannot be wrong or empty");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Date of birth cannot be in the future");
        }

    }
}
