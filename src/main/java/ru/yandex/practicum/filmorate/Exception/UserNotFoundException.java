package ru.yandex.practicum.filmorate.Exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("User  with ID " + userId + " not found.");
    }
}