package ru.yandex.practicum.filmorate.Exception;

public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException(Long id) {
        super("Фильм с ID " + id + " не найден");
    }
}
