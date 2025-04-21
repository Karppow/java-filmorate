package ru.yandex.practicum.filmorate.Exception;

public class LikeNotFoundException extends RuntimeException {
    public LikeNotFoundException(Integer filmId, Integer userId) {
        super("Пользователь с ID " + userId + " не ставил лайк фильму с ID " + filmId);
    }
}
