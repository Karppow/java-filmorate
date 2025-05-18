package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FutureBirthdayException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.info("Creating user: {}", user);

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new FutureBirthdayException("Дата рождения не может быть в будущем.");
        }

        User createdUser = userService.addUser(user);
        log.info("User created with ID: {}", createdUser.getId());
        return ResponseEntity.status(201).body(createdUser);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.info("Updating user with ID: {}", user.getId());
        User updatedUser = userService.updateUser(user);
        log.info("User updated: {}", updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable @Positive Integer id) {
        log.info("Getting user with ID {}", id);
        User user = userService.getUser(id);
        log.info("User found: {}", user);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        log.info("Getting all users");
        List<User> users = userService.getUsers();
        log.info("Total users found: {}", users.size());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable @Positive Integer id, @PathVariable @Positive Integer friendId) {
        log.info("Add friend called: userId={}, friendId={}", id, friendId);
        userService.addFriend(id, friendId);
        User user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable @Positive Integer id, @PathVariable @Positive Integer friendId) {
        log.info("Removing friend with ID {} from user with ID {}", friendId, id);
        userService.removeFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<Set<User>> getFriends(@PathVariable @Positive Integer userId) {
        log.info("Getting friends for user with ID {}", userId);
        Set<User> friends = userService.getFriends(userId);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Set<User>> getCommonFriends(
            @PathVariable @Positive Integer id,
            @PathVariable @Positive Integer otherId) {
        log.info("Getting common friends between user with ID {} and user with ID {}", id, otherId);
        Set<User> commonFriends = userService.getCommonFriendsAsUsers(id, otherId);
        return ResponseEntity.ok(commonFriends);
    }
}