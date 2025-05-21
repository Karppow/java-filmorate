package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.LikeAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final InMemoryFilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public FilmService(InMemoryFilmStorage filmStorage, MpaStorage mpaStorage, GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public Film addFilm(Film film) {
        // Проверяем, что mpa_id существует
        Integer mpaId = film.getMpa().getId();
        if (!mpaStorage.existsById(mpaId)) {
            throw new MpaNotFoundException("MPA с ID " + mpaId + " не существует");
        }
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(Integer id) {
        filmStorage.deleteFilm(id);
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        for (Film film : films) {
            film.setMpa(mpaStorage.getMpa(film.getMpa().getId()));
            Set<Genre> fullGenres = film.getGenres().stream()
                    .map(g -> genreStorage.getGenre(g.getId()))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            film.setGenres(new ArrayList<>(fullGenres));
        }
        return films;
    }

    private Film findFilmById(Integer filmId) {
        Film film = filmStorage.getFilm(filmId);
        if (film == null) {
            throw new FilmNotFoundException(filmId);
        }
        film.setMpa(mpaStorage.getMpa(film.getMpa().getId()));
        Set<Genre> fullGenres = film.getGenres().stream()
                .map(g -> genreStorage.getGenre(g.getId()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        film.setGenres(new ArrayList<>(fullGenres));
        return film;
    }

    public boolean filmExists(Integer filmId) {
        try {
            findFilmById(filmId); // Проверяем, существует ли фильм
            return true; // Если найден, возвращаем true
        } catch (FilmNotFoundException e) {
            return false; // Если не найден, возвращаем false
        }
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = findFilmById(filmId); // Найти фильм или выбросить исключение

        // Проверяем, ставил ли пользователь лайк ранее
        if (film.getLikes().contains(userId)) {
            throw new LikeAlreadyExistsException("Пользователь с ID " + userId + " уже поставил лайк фильму с ID " + filmId); // 400 Bad Request
        }
        film.getLikes().add(userId); // Добавляем лайк
        log.info("Пользователь с ID {} поставил лайк фильму с ID {}", userId, filmId);
    }

    public boolean removeLike(Integer filmId, Integer userId) {
        Film film = findFilmById(filmId); // Найти фильм или выбросить исключение

        // Проверяем, ставил ли пользователь лайк
        if (film.getLikes().remove(userId)) {
            log.info("Пользователь с ID {} удалил лайк у фильма с ID {}", userId, filmId);
            return true; // Успешно удалено
        } else {
            throw new LikeNotFoundException("Пользователь с ID " + userId + " не ставил лайк фильму с ID " + filmId); // 404 Not Found
        }
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film f) -> -f.getLikes().size())
                        .thenComparing(Film::getName))  // Дополнительная сортировка по названию
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilm(Integer id) {
        return findFilmById(id);
    }
}
