package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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
        User existingUser  = userStorage.getUser(user.getId());
        if (existingUser  == null) {
            throw new UserNotFoundException("Пользователь с ID " + user.getId() + " не найден");
        }
        return userStorage.updateUser(user);
    }

    public void addFriend(Integer userId, Integer friendId) {
        // Проверка на добавление самого себя в друзья
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Пользователь не может добавить себя в друзья");
        }

        // Получение пользователей из хранилища
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        // Проверка существования пользователей
        if (user == null) {
            log.error("Пользователь с ID {} не найден", userId);
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден");
        }
        if (friend == null) {
            log.error("Друг с ID {} не найден", friendId);
            throw new UserNotFoundException("Друг с ID " + friendId + " не найден");
        }

        // Проверка, не является ли друг уже другом пользователя
        if (user.getFriends().contains(friendId)) {
            log.warn("Пользователь с ID {} уже является другом пользователя с ID {}", userId, friendId);
            throw new IllegalArgumentException("Пользователь с ID " + userId + " уже является другом пользователя с ID " + friendId);
        }

        // Добавление друга
        user.addFriend(friendId);  // Добавляем ID друга
        friend.addFriend(userId);   // Добавляем ID пользователя в друзья друга

        // Обновление пользователей в хранилище
        userStorage.updateUser(user);  // Явное обновление пользователя в хранилище
        userStorage.updateUser(friend); // Явное обновление друга в хранилище

        // Логирование успешной операции
        log.info("Успешно добавлен друг с ID {} к пользователю с ID {}", friendId, userId);
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

        // Изменяем код ошибки с 400 на 404
        if (!user.getFriends().contains(friendId)) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не является другом пользователя с ID " + friendId);
        }

        user.removeFriend(friendId);
        friend.removeFriend(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);
        log.info("Удален друг с ID {} у пользователя с ID {}", friendId, userId);
    }

    public Set<Integer> getCommonFriends(Integer userId1, Integer userId2) {
        User user1 = userStorage.getUser(userId1);
        User user2 = userStorage.getUser(userId2);

        // Проверка на существование пользователей
        if (user1 == null) {
            throw new UserNotFoundException("Пользователь с ID " + userId1 + " не найден");
        }
        if (user2 == null) {
            throw new UserNotFoundException("Пользователь с ID " + userId2 + " не найден");
        }

        Set<Integer> commonFriends = new HashSet<>(user1.getFriends());
        commonFriends.retainAll(user2.getFriends());  // Получаем пересечение друзей
        return commonFriends;
    }

    public Set<User> getFriends(Integer userId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден");
        }

        Set<Integer> friendIds = user.getFriends();
        if (friendIds == null || friendIds.isEmpty()) {
            log.warn("Пользователь с ID {} не имеет друзей.", userId);
            return new HashSet<>(); // Возвращаем пустой набор, если друзей нет
        }

        Set<User> friends = new HashSet<>();
        for (Integer friendId : friendIds) {
            User friend = userStorage.getUser(friendId);
            if (friend != null) {
                friends.add(friend);
            } else {
                log.warn("Друг с ID {} не найден для пользователя с ID {}", friendId, userId);
            }
        }

        log.info("Найдено {} друзей для пользователя с ID {}", friends.size(), userId);
        return friends;
    }

    public Set<User> getCommonFriendsAsUsers(Integer userId1, Integer userId2) {
        // Получаем общие ID друзей
        Set<Integer> commonFriendIds = getCommonFriends(userId1, userId2);

        Set<User> commonFriends = new HashSet<>();

        // Преобразуем каждый ID друга в объект User
        for (Integer id : commonFriendIds) {
            User user = userStorage.getUser(id);  // Получаем объект пользователя по ID
            if (user != null) {
                commonFriends.add(user);
            }
        }
        return commonFriends;
    }

    public boolean userExists(Integer userId) {
        return getUser(userId) != null; // Проверка на существование пользователя
    }
}

