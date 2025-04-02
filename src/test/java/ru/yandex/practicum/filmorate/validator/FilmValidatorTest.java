package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmValidatorTest {

    private FilmValidator filmValidator;

    @BeforeEach
    public void setUp() {
        filmValidator = new FilmValidator();
    }

    @Test
    public void testFilmValidation_NameIsEmpty() {
        Film film = new Film();
        film.setName("");
        ValidationException exception = assertThrows(ValidationException.class, () -> filmValidator.validate(film));
        assertEquals("Название фильма не должно быть пустым", exception.getMessage());
    }
}