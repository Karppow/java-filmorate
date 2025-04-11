package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final InMemoryFilmStorage filmStorage;

    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film getFilm(Long id) {
        return filmStorage.getFilm(id);
    }


    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);
        if (film != null) {
            film.getLikes().add(userId);
        }
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);
        if (film != null) {
            film.getLikes().remove(userId);
        }
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
