package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final AtomicInteger currentId = new AtomicInteger(1);
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);

    @Override
    public User addUser(User user) {
        int id = currentId.getAndIncrement();
        user.setId(id);
        user.setFriends(new HashSet<>());
        users.put(id, user);
        log.info("Добавлен пользователь с ID {}", id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkUserExists(user.getId());
        users.put(user.getId(), user);
        log.info("Обновлен пользователь с ID {}", user.getId());
        return user;
    }

    @Override
    public User getUser(Integer id) {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с ID " + id + " не найден");
        }
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Нельзя добавить самого себя в друзья");
        }
        User user = getUser(userId);
        User friend = getUser(friendId);

        if (user.getFriends().contains(friendId)) {
            log.info("Пользователь с ID {} уже добавил друга с ID {}", userId, friendId);
            return;
        }

        user.getFriends().add(friendId);
        log.info("Пользователь с ID {} добавил друга с ID {}", userId, friendId);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Нельзя удалить самого себя из друзей");
        }
        User user = getUser(userId);
        User friend = getUser(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.info("Пользователь с ID {} удалил друга с ID {}", userId, friendId);
    }

    @Override
    public List<Integer> getCommonFriends(Integer userId1, Integer userId2) {
        User user1 = getUser(userId1);
        User user2 = getUser(userId2);

        Set<Integer> common = new HashSet<>(user1.getFriends());
        common.retainAll(user2.getFriends());

        return new ArrayList<>(common);
    }

    @Override
    public boolean userExists(Integer userId) {
        return users.containsKey(userId);
    }

    @Override
    public List<User> getFriends(Integer userId) {
        User user = getUser(userId);
        List<User> friends = new ArrayList<>();
        for (Integer id : user.getFriends()) {
            User friend = users.get(id);
            if (friend != null) {
                friends.add(friend);
            }
        }
        return friends;
    }

    @Override
    public Set<User> getUsersByIds(Set<Integer> ids) {
        Set<User> result = new HashSet<>();
        for (Integer id : ids) {
            User user = users.get(id);
            if (user != null) {
                result.add(user);
            }
        }
        return result;
    }

    private void checkUserExists(Integer userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден");
        }
    }
}

