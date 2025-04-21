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
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validator.ErrorResponse;

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

        // Проверка на дату рождения (будущая дата)
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        User createdUser = userService.addUser(user);
        log.info("User created with ID: {}", createdUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping
    public ResponseEntity<Object> updateUser(@Valid @RequestBody User user) {
        log.info("Updating user with ID: {}", user.getId());

        try {
            User updatedUser = userService.updateUser(user);
            log.info("User updated: {}", updatedUser);
            return ResponseEntity.ok(updatedUser);
        } catch (ResponseStatusException e) {
            log.error("Error updating user: {}", e.getMessage(), e);
            // Используйте ResponseEntity<Object> для гибкости в ответах
            return ResponseEntity.status(e.getStatusCode()).body(new ErrorResponse(e.getReason()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        log.info("Getting user with ID {}", id);
        User user = userService.getUser(id);

        if (user == null) {
            log.warn("User with ID {} not found", id);
            throw new UserNotFoundException(id);  // Используем исключение для возврата 404
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
        try {
            userService.addFriend(id, friendId);
        } catch (ResponseStatusException e) {
            log.error("Error occurred while adding friend: {}", e.getMessage());
            throw e;  // Пробрасываем исключение дальше
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable @Positive Integer id, @PathVariable @Positive Integer friendId) {
        log.info("Removing friend with ID {} from user with ID {}", friendId, id);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<Set<User>> getFriends(@PathVariable @Positive Integer userId) {
        Set<User> friends = userService.getFriends(userId);
        return ResponseEntity.ok(friends); // Возвращаем [] вместо null
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Set<User>> getCommonFriends(
            @PathVariable Integer id,
            @PathVariable Integer otherId) {
        log.info("Getting common friends between user with ID {} and user with ID {}", id, otherId);
        Set<User> commonFriends = userService.getCommonFriendsAsUsers(id, otherId);  // Теперь используем новый метод
        log.info("Common friends found: {}", commonFriends);
        return ResponseEntity.ok(commonFriends);
    }
}
