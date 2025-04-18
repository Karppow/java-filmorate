package ru.yandex.practicum.filmorate.Exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Integer userId) {
        super("User  with ID " + userId + " not found.");
    }
}