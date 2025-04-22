package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.LikeAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.LikeNotFoundException;
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
            throw new FilmNotFoundException(filmId); // Бросаем исключение, если фильм не найден
        }
        return film; // Возвращаем фильм, если он найден
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
        return findFilmById(id); // Теперь метод findFilmById выбросит исключение, если фильм не найден
    }
}
