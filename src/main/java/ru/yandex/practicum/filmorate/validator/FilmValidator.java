package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Date;
import java.util.Objects;

public class FilmValidator {
    public void validate(Film film) {
        if (Objects.isNull(film.getName()) || film.getName().isEmpty()) {
            throw new ValidationException("Название фильма Пустыня");
        }
        if (Objects.nonNull(film.getDescription()) && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (Objects.nonNull(film.getReleaseDate()) && film.getReleaseDate().before(new Date(1895, 12, 28))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (Objects.nonNull(film.getDuration()) && film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}