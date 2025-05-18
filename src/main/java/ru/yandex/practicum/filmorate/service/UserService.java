package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        validateUser(user);
        return userStorage.addUser(user);
    }

    public User getUser(Integer id) {
        User user = userStorage.getUser(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с ID " + id + " не найден");
        }
        return user;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User updateUser(User user) {
        validateUser(user);
        User existingUser = userStorage.getUser(user.getId());
        if (existingUser == null) {
            throw new UserNotFoundException("Пользователь с ID " + user.getId() + " не найден");
        }
        return userStorage.updateUser(user);
    }

    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new ValidationException("Нельзя добавить себя в друзья");
        }

        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        if (user == null) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден");
        }
        if (friend == null) {
            throw new UserNotFoundException("Пользователь с ID " + friendId + " не найден");
        }

        if (user.getFriends() != null && user.getFriends().contains(friendId)) {
            log.info("Пользователь {} уже добавил друга {}", userId, friendId);
            return;
        }

        userStorage.addFriend(userId, friendId);
        log.info("Пользователь {} добавил друга {}", userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        if (user == null) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден");
        }
        if (friend == null) {
            throw new UserNotFoundException("Друг с ID " + friendId + " не найден");
        }

        userStorage.removeFriend(userId, friendId);
        log.info("Пользователь {} удалил друга {}", userId, friendId);
    }

    public Set<Integer> getCommonFriendIds(Integer userId1, Integer userId2) {
        return new HashSet<>(userStorage.getCommonFriends(userId1, userId2));
    }

    public Set<User> getFriends(Integer userId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден");
        }

        Set<Integer> friendIds = user.getFriends();
        if (friendIds == null || friendIds.isEmpty()) {
            return Collections.emptySet();
        }

        return userStorage.getUsersByIds(friendIds);
    }

    public Set<User> getCommonFriendsAsUsers(Integer userId1, Integer userId2) {
        Set<Integer> commonFriendIds = new HashSet<>(userStorage.getCommonFriends(userId1, userId2));
        if (commonFriendIds.isEmpty()) {
            return Collections.emptySet();
        }
        return userStorage.getUsersByIds(commonFriendIds);
    }

    public boolean userExists(Integer userId) {
        return userStorage.userExists(userId);
    }

    private void validateUser(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный email");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(java.time.LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}

