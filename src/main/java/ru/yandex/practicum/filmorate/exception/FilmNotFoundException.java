package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException(Integer filmId) {
        super("Фильм с ID " + filmId + " не найден."); // Преобразуем Integer в String
    }
}
