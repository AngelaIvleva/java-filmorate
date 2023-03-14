package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAllUsers() {
        return new ArrayList<>(userService.getUsers().values());
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") Long userId,
                                       @PathVariable("otherId") Long friendId) {
        return userService.getCommonFriends(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendList(@PathVariable("id") Long userId) {
        return userService.getFriendList(userId);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        validation(user);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        validation(user);
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Long userId,
                          @PathVariable Long friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long userId,
                             @PathVariable Long friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @DeleteMapping
    public void deleteUserById(@RequestBody Long id) {
        userService.deleteUserById(id);
    }

    public void validation(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException("User cannot be null");
        }
        if  (user.getName() == null || user.getName().isBlank()) {
            log.info("Name is empty. Login is set as Name");
            user.setName(user.getLogin());
        }
        if (user.getEmail() == null || user.getName().isBlank()){
            throw new ValidationException("Email cannot be empty");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Email doesn't contain @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Login cannot be wrong or empty");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Date of birth cannot be in the future");
        }

    }
}
