package ru.yandex.practicum.filmorate.Exception;

public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException(long filmId) {
        super("Фильм с ID " + filmId + " не найден");
    }
}
