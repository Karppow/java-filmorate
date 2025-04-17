package ru.yandex.practicum.filmorate.service;

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

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User getUser(Long id) {
        return userStorage.getUser(id);
    }

    public List<User> getUsers() {
        return userStorage.getUsers(); // Добавьте этот метод
    }

    public User updateUser(User user) {
        User existingUser = userStorage.getUser(user.getId());
        if (existingUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return userStorage.updateUser(user);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + userId + " not found");
        }

        if (friend == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend with ID " + friendId + " not found");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        if (user != null && friend != null) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
        }
    }

    public Set<Long> getCommonFriends(Long userId1, Long userId2) {
        User user1 = userStorage.getUser(userId1);
        User user2 = userStorage.getUser(userId2);
        if (user1 != null && user2 != null) {
            Set<Long> commonFriends = new HashSet<>(user1.getFriends());
            commonFriends.retainAll(user2.getFriends());
            return commonFriends;
        }
        return new HashSet<>();
    }

    public Set<User> getFriends(Long userId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        Set<Long> friendIds = user.getFriends();
        Set<User> friends = new HashSet<>();

        for (Long friendId : friendIds) {
            User friend = userStorage.getUser(friendId);
            if (friend != null) {
                friends.add(friend);
            }
        }
        return friends;
    }
}
