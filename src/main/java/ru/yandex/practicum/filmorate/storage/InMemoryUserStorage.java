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

    @Override
    public List<User> getFriends(Integer userId) {
        User user = getUser(userId); // Получаем пользователя по ID
        if (user == null) {
            return new ArrayList<>(); // Если пользователь не найден, возвращаем пустой список
        }

        List<User> friendsList = new ArrayList<>();
        for (Integer friendId : user.getFriends()) {
            User friend = getUser(friendId);
            if (friend != null) {
                friendsList.add(friend); // Добавляем друга в список, если он найден
            }
        }
        return friendsList; // Возвращаем список друзей
    }
}