package ru.yandex.practicum.filmorate.validator;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidatorTest {
    private FilmValidator filmValidator;

    @BeforeEach
    public void setUp() {
        filmValidator = new FilmValidator();
    }

    @Test
    public void testFilmValidation() {
        Film film = new Film();
        film.setName("");
        try {
            filmValidator.validate(film);
            fail("Ожидается ValidationException");
        } catch (ValidationException e) {
            assertEquals("Название фильма不能为空", e.getMessage());
        }
    }
}