package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    User getUser(Integer id);

    List<User> getUsers();

    User addFriend(Integer userId, Integer friendId);

}
