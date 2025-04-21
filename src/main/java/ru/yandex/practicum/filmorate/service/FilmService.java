package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.Exception.LikeAlreadyExistsException;
import ru.yandex.practicum.filmorate.Exception.LikeNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final InMemoryFilmStorage filmStorage;

    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(Long id) {
        filmStorage.deleteFilm(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);
        if (film == null) {
            log.error("Фильм с ID {} не найден", filmId);
            throw new FilmNotFoundException(filmId);  // Бросаем исключение, если фильм не найден
        }

        // Проверка на уже существующий лайк
        if (film.getLikes().contains(userId)) {
            log.warn("Пользователь с ID {} уже поставил лайк фильму с ID {}", userId, filmId);
            throw new RuntimeException("Лайк уже существует.");
        }

        film.getLikes().add(userId);
        log.info("Пользователь с ID {} поставил лайк фильму с ID {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);
        if (film == null) {
            log.error("Фильм с ID {} не найден", filmId);
            throw new FilmNotFoundException(filmId);
        }

        // Проверка на существующий лайк
        if (!film.getLikes().contains(userId)) {
            log.warn("Пользователь с ID {} не ставил лайк фильму с ID {}", userId, filmId);
            throw new LikeNotFoundException(filmId, userId);
        }

        film.getLikes().remove(userId);
        log.info("Пользователь с ID {} удалил лайк с фильма с ID {}", userId, filmId);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film f) -> -f.getLikes().size())
                        .thenComparing(Film::getName))  // Дополнительная сортировка по названию
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilm(Long id) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            log.error("Фильм с ID {} не найден", id);  // Логируем ошибку
            throw new FilmNotFoundException(id);  // Бросаем исключение, если фильм не найден
        }
        return film;
    }
}

