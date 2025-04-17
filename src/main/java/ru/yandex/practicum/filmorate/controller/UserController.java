package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.Exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.FriendRequest;
import ru.yandex.practicum.filmorate.model.CommonFriendsRequest;
import ru.yandex.practicum.filmorate.service.UserService;

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
        User createdUser  = userService.addUser(user);
        log.info("User  created with ID: {}", createdUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.info("Updating user with ID: {}", user.getId());

        User updatedUser = userService.updateUser(user);

        // Проверяем, был ли пользователь найден
        if (updatedUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        log.info("User updated: {}", updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        log.info("Getting user with ID {}", id);
        User user = userService.getUser(id);
        if (user == null) {
            log.warn("User with ID {} not found", id);
            throw new UserNotFoundException(id);
        }
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

    @PutMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        log.info("Adding friend with ID {} to user with ID {}", friendId, userId);
        userService.addFriend(userId, friendId);
        log.info("Friend with ID {} added to user with ID {}", friendId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/friends")
    public ResponseEntity<Void> removeFriend(@RequestBody FriendRequest friendRequest) {
        Long userId = friendRequest.getUserId();
        Long friendId = friendRequest.getFriendId();
        log.info("Removing friend with ID {} from user with ID {}", friendId, userId);
        userService.removeFriend(userId, friendId);
        log.info("Friend with ID {} removed from user with ID {}", friendId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Set<User>> getFriends(@PathVariable Long id) {
        log.info("Getting friends for user with ID {}", id);

        // Проверяем, существует ли пользователь
        if (!userService.existsById(id)) {
            log.warn("User with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Возвращаем 404, если пользователь не найден
        }

        Set<User> friends = userService.getFriends(id);

        if (friends.isEmpty()) {
            log.warn("No friends found for user with ID {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Возвращаем 404, если друзей нет
        }

        log.info("Friends found for user with ID {}: {}", id, friends);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/friends/common")
    public ResponseEntity<Set<Long>> getCommonFriends(@RequestBody CommonFriendsRequest commonFriendsRequest) {
        Long id = commonFriendsRequest.getId();
        Long otherId = commonFriendsRequest.getOtherId();
        log.info("Getting common friends between user with ID {} and user with ID {}", id, otherId);
        Set<Long> commonFriends = userService.getCommonFriends(id, otherId);
        log.info("Common friends found: {}", commonFriends);
        return ResponseEntity.ok(commonFriends);
    }
}
