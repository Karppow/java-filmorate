package ru.yandex.practicum.filmorate.validator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Objects;

@Component
public class FilmValidator {
    public void validate(Film film) {
        if (Objects.isNull(film.getName()) || film.getName().trim().isEmpty()) {
            throw new ValidationException("Название фильма не должно быть пустым");
        }
        if (Objects.nonNull(film.getDescription()) && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (Objects.nonNull(film.getReleaseDate()) && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (Objects.nonNull(film.getDuration()) && film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}
