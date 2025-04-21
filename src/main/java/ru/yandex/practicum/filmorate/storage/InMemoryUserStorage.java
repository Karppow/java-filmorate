package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer currentId = 1;

    @Override
    public User addUser(User user) {
        user.setId(currentId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            return null;
        }

        // Обновляем пользователя в хранилище
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(Integer id) {
        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public boolean addFriend(Integer userId, Integer friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        if (user == null || friend == null) {
            return false; // Один из пользователей не найден
        }

        user.addFriend(friendId); // Метод добавления друга в классе User
        return true; // Успех
    }

    @Override
    public List<User> getFriends(Integer userId) {
        User user = getUser(userId);
        if (user == null) {
            return new ArrayList<>();  // Пользователь не найден
        }

        Set<User> friendsSet = new HashSet<>();
        for (Integer friendId : user.getFriends()) {
            User friend = getUser(friendId);
            if (friend != null) {
                friendsSet.add(friend);
            }
        }
        return new ArrayList<>(friendsSet);  // Возвращаем уникальных друзей
    }
}