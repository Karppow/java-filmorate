package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    User getUser(Integer id);

    List<User> getUsers();

    void addFriend(Integer userId, Integer friendId);

    void removeFriend(Integer userId, Integer friendId);

    List<Integer> getCommonFriends(Integer userId1, Integer userId2);

    boolean userExists(Integer userId);

    List<User> getFriends(Integer userId);

    Set<User> getUsersByIds(Set<Integer> ids);
}
