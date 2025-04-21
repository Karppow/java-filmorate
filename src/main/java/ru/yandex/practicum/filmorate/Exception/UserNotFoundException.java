package ru.yandex.practicum.filmorate.Exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Integer id) {
        super("Пользователь с ID " + id + " не найден");
    }
}