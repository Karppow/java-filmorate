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

    public void deleteFilm(Integer id) {
        filmStorage.deleteFilm(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    private Film findFilmById(Integer filmId) {
        Film film = filmStorage.getFilm(filmId);
        if (film == null) {
            log.error("Фильм с ID {} не найден", filmId);
        }
        return film; // Возвращаем null, если фильм не найден
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = findFilmById(filmId);
        if (film == null) {
            throw new FilmNotFoundException(filmId); // 404 Not Found
        }

        // Проверяем, ставил ли пользователь лайк ранее
        if (!film.getLikes().contains(userId)) {
            film.getLikes().add(userId); // Добавляем лайк
        } else {
            throw new LikeAlreadyExistsException(filmId, userId); // 400 Bad Request
        }
    }

    public boolean removeLike(Integer filmId, Integer userId) {
        Film film = findFilmById(filmId);
        if (film == null) {
            throw new FilmNotFoundException(filmId); // 404 Not Found
        }

        // Проверяем, ставил ли пользователь лайк
        if (film.getLikes().contains(userId)) {
            film.getLikes().remove(userId); // Удаляем лайк
            return true; // Успешно удалено
        } else {
            throw new LikeNotFoundException(userId,filmId); // 404 Not Found
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
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            log.error("Фильм с ID {} не найден", id);  // Логируем ошибку
            throw new FilmNotFoundException(id);  // Бросаем исключение, если фильм не найден
        }
        return film;
    }
}

