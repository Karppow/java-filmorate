package ru.yandex.practicum.filmorate.Exception;

public class LikeAlreadyExistsException extends RuntimeException {
    public LikeAlreadyExistsException(Long filmId, Long userId) {
        super("Пользователь с ID " + userId + " уже поставил лайк фильму с ID " + filmId);
    }
}
