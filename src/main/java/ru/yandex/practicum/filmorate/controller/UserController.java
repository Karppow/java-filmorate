package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FutureBirthdayException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
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

        // Проверка на дату рождения (будущая дата)
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new FutureBirthdayException("Дата рождения не может быть в будущем."); // Обновлено
        }

        User createdUser  = userService.addUser(user);
        log.info("User  created with ID: {}", createdUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser );
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.info("Updating user with ID: {}", user.getId());
        User updatedUser  = userService.updateUser(user);
        log.info("User  updated: {}", updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable @Positive Integer id) {
        log.info("Getting user with ID {}", id);
        User user = userService.getUser(id);

        if (user == null) {
            log.warn("User  with ID {} not found", id);
            throw new UserNotFoundException("Пользователь с ID " + id + " не найден."); // Обновлено
        }

        log.info("User  found: {}", user);
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
    public ResponseEntity<Void> addFriend(@PathVariable @Positive Integer id, @PathVariable @Positive Integer friendId) {
        log.info("Adding friend with ID {} to user with ID {}", friendId, id);
        userService.addFriend(id, friendId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable @Positive Integer id, @PathVariable @Positive Integer friendId) {
        log.info("Removing friend with ID {} from user with ID {}", friendId, id);

        // Получаем пользователя и его друга
        User user = userService.getUser(id);
        User friend = userService.getUser(friendId);

        // Проверяем, что оба пользователя существуют
        if (user == null) {
            throw new UserNotFoundException("Пользователь с ID " + id + " не найден."); // Обновлено
        }
        if (friend == null) {
            throw new UserNotFoundException("Пользователь с ID " + friendId + " не найден."); // Обновлено
        }

        // Проверяем, есть ли друг в списке друзей
        if (!user.getFriends().contains(friendId)) {
            log.info("User  with ID {} is not friends with friend with ID {}", id, friendId);
            return ResponseEntity.ok().build(); // Возвращаем 200 OK, даже если друга нет в списке
        }

        // Удаляем друга
        userService.removeFriend(id, friendId); // Удаляем друга через сервис
        log.info("Successfully removed friend with ID {} from user with ID {}", friendId, id);

        return ResponseEntity.ok().build(); // Возвращаем 200 OK, если друг был успешно удален
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<Set<User>> getFriends(@PathVariable @Positive Integer userId) {
        log.info("Getting friends for user with ID {}", userId);
        Set<User> friends = userService.getFriends(userId);

        if (friends == null || friends.isEmpty()) {
            // Если нет друзей, можно вернуть пустой список
            return ResponseEntity.ok(friends);
        }

        return ResponseEntity.ok(friends); // Возвращаем список друзей
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Set<User>> getCommonFriends(
            @PathVariable @Positive Integer id,
            @PathVariable @Positive Integer otherId) {
        log.info("Getting common friends between user with ID {} and user with ID {}", id, otherId);
        Set<User> commonFriends = userService.getCommonFriendsAsUsers(id, otherId);  // Теперь используем новый метод
        log.info("Common friends found: {}", commonFriends);
        return ResponseEntity.ok(commonFriends);
    }
}

