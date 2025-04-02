package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        logger.info("Creating user: {}", user);
        long id = users.size() + 1;
        user.setId(id);
        users.put(id, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        logger.info("Updating user: {}", user);

        long id = user.getId(); // Получаем ID из объекта пользователя

        if (users.containsKey(id)) {
            users.put(id, user);
            logger.info("User  with ID {} updated successfully", id);
            return ResponseEntity.ok(user); // Возвращаем обновленного пользователя
        } else {
            logger.warn("User  with ID {} not found", id);
            return ResponseEntity.notFound().build(); // Возвращаем 404 без тела
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Getting all users");
        return ResponseEntity.ok(List.copyOf(users.values()));
    }
}
