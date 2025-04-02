package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Creating user: {}", user);
        valid(user);
        long id = users.size() + 1;
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.info("Updating user with ID: {}", user.getId());

        if (users.containsKey(user.getId())) {
            valid(user);
            users.put(user.getId(), user);
            log.info("User  with ID {} updated successfully", user.getId());
            return ResponseEntity.ok(user);
        } else {
            log.warn("User  with ID {} not found. Existing users: {}", user.getId(), users.keySet());
            throw new UserNotFoundException(user.getId()); // выбрасываем исключение
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Getting all users");
        return ResponseEntity.ok(List.copyOf(users.values()));
    }

    private void valid(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin()); // Устанавливаем имя равным логину, если имя отсутствует
            log.info("Name was empty, setting name to login: {}", user.getLogin());
        }
    }
}
