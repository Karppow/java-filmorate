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
    public ResponseEntity<ErrorResponse> addFriend(@PathVariable @Positive Integer id, @PathVariable @Positive Integer friendId) {
        try {
            userService.addFriend(id, friendId);
            return ResponseEntity.noContent().build();  // 204 No Content
        } catch (ResponseStatusException e) {
            log.error("Ошибка при добавлении друга с friendId {}: {}", friendId, e.getMessage());
            // Возвращаем ошибку в формате JSON
            ErrorResponse errorResponse = new ErrorResponse("Ошибка: " + e.getReason());
            return ResponseEntity.status(e.getStatusCode())
                    .body(errorResponse);  // Возвращаем ErrorResponse в теле
        } catch (Exception e) {
            log.error("Неожиданная ошибка: {}", e.getMessage());
            // Возвращаем неожиданные ошибки тоже в формате JSON
            ErrorResponse errorResponse = new ErrorResponse("Неожиданная ошибка при добавлении друга");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);  // Возвращаем ErrorResponse в теле
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable @Positive Integer id, @PathVariable @Positive Integer friendId) {
        try {
            log.info("Removing friend with ID {} from user with ID {}", friendId, id);

            // Получаем пользователя и его друга
            User user = userService.getUser(id);
            User friend = userService.getUser(friendId);

            // Проверяем, что оба пользователя существуют
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
            if (friend == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend not found");
            }

            // Проверяем, есть ли друг в списке друзей
            if (!user.getFriends().contains(friendId)) {
                // Если друг не найден, возвращаем 200 OK вместо 404, чтобы это считалось успешным
                log.info("User with ID {} is not friends with friend with ID {}", id, friendId);
                return ResponseEntity.ok().build();  // Возвращаем 200 OK, даже если друга нет в списке
            }

            // Удаляем друга
            userService.removeFriend(id, friendId); // Удаляем друга через сервис
            log.info("Successfully removed friend with ID {} from user with ID {}", friendId, id);

            return ResponseEntity.ok().build();  // Возвращаем 200 OK, если друг был успешно удален

        } catch (ResponseStatusException e) {
            log.error("Error removing friend: {}", e.getMessage(), e);
            throw e;  // Пробрасываем исключение дальше
        } catch (Exception e) {
            log.error("Unexpected error occurred: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", e);
        }
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<Object> getFriends(@PathVariable @Positive Integer userId) {
        try {
            Set<User> friends = userService.getFriends(userId);

            if (friends == null || friends.isEmpty()) {
                // Если нет друзей, можно вернуть пустой список, чтобы избежать ошибки
                return ResponseEntity.ok(friends);
            }

            return ResponseEntity.ok(friends); // Возвращаем список друзей
        } catch (ResponseStatusException e) {
            log.error("Ошибка при получении списка друзей для пользователя с ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(new ErrorResponse(e.getReason())); // Возвращаем ошибку с подробностями
        } catch (Exception e) {
            log.error("Неожиданная ошибка: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Неожиданная ошибка при получении друзей"));
        }
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
