package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User getUser(Integer id) {
        return userStorage.getUser(id);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public boolean existsById(Integer id) {
        return userStorage.getUser(id) != null;
    }

    public User updateUser(User user) {
        User existingUser = userStorage.getUser(user.getId());
        if (existingUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return userStorage.updateUser(user);
    }

    public void addFriend(Integer userId, Integer friendId) {
        // Проверка на добавление самого себя в друзья
        if (userId.equals(friendId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User  cannot add themselves as a friend");
        }

        // Получение пользователей из хранилища
        User user = userStorage.getUser (userId);
        User friend = userStorage.getUser (friendId);

        // Проверка существования пользователей
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User  with ID " + userId + " not found");
        }

        if (friend == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend with ID " + friendId + " not found");
        }

        // Проверка, не является ли друг уже другом пользователя
        if (user.getFriends().contains(friendId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User  with ID " + userId + " is already friends with user with ID " + friendId);
        }

        // Добавление друга
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        // Обновление пользователей в хранилище
        userStorage.updateUser (user);
        userStorage.updateUser (friend);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        if (user != null && friend != null) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
            userStorage.updateUser(user);
            userStorage.updateUser(friend);
        }
    }

    public Set<Integer> getCommonFriends(Integer userId1, Integer userId2) {
        User user1 = userStorage.getUser(userId1);
        User user2 = userStorage.getUser(userId2);
        if (user1 != null && user2 != null) {
            Set<Integer> commonFriends = new HashSet<>(user1.getFriends());
            commonFriends.retainAll(user2.getFriends());
            return commonFriends;
        }
        return new HashSet<>();
    }

    public Set<User> getFriends(Integer userId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User  not found");
        }

        Set<Integer> friendIds = user.getFriends();
        Set<User> friends = new HashSet<>();

        if (friendIds == null) {
            log.warn("User  with ID {} has no friends.", userId);
            return friends; // Возвращаем пустой набор, если друзей нет
        }

        for (Integer friendId : friendIds) {
            User friend = userStorage.getUser(friendId);
            if (friend != null) {
                friends.add(friend);
            } else {
                log.warn("Friend with ID {} not found for user with ID {}", friendId, userId);
            }
        }

        log.info("Found {} friends for user with ID {}", friends.size(), userId);
        return friends;
    }

}
