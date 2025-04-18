package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
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

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable @Positive Integer id, @PathVariable @Positive Integer friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/friends")
    public ResponseEntity<Void> removeFriend(@RequestBody FriendRequest friendRequest) {
        Integer userId = friendRequest.getUserId();
        Integer friendId = friendRequest.getFriendId();
        log.info("Removing friend with ID {} from user with ID {}", friendId, userId);
        userService.removeFriend(userId, friendId);
        log.info("Friend with ID {} removed from user with ID {}", friendId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Set<User>> getFriends(@PathVariable @Positive Integer userId) {
        Set<User> friends = userService.getFriends(userId);
        if (friends.isEmpty()) {
            return ResponseEntity.noContent().build(); // Возвращаем 204 No Content, если друзей нет
        }
        return ResponseEntity.ok(friends); // Возвращаем 200 OK и список друзей
    }

    @GetMapping("/friends/common")
    public ResponseEntity<Set<Integer>> getCommonFriends(@RequestBody CommonFriendsRequest commonFriendsRequest) {
        Integer id = commonFriendsRequest.getId();
        Integer otherId = commonFriendsRequest.getOtherId();
        log.info("Getting common friends between user with ID {} and user with ID {}", id, otherId);
        Set<Integer> commonFriends = userService.getCommonFriends(id, otherId);
        log.info("Common friends found: {}", commonFriends);
        return ResponseEntity.ok(commonFriends);
    }
}
