package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface FilmStorage {
    Film addFilm(Film film);
    Film updateFilm(Film film);
    void deleteFilm(long id);
    Film getFilm(long id);
    List<Film> getAllFilms();
}
