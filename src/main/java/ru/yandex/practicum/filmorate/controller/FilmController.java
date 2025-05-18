package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final FilmValidator filmValidator;
    private final UserService userService;

    @Autowired
    public FilmController(FilmService filmService, FilmValidator filmValidator, UserService userService) {
        this.filmService = filmService;
        this.filmValidator = filmValidator;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        filmValidator.validate(film);
        Film createdFilm = filmService.addFilm(film);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        List<Film> films = filmService.getAllFilms();
        if (films.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(films);
    }

    @PutMapping
    public ResponseEntity<Object> updateFilm(@RequestBody Film film) {
        if (film.getId() == null) {
            log.error("ID фильма не может быть null при обновлении");
            return ResponseEntity.badRequest().body(Map.of("error", "ID фильма не может быть null"));
        }
        try {
            Film updatedFilm = filmService.updateFilm(film);
            return ResponseEntity.ok(updatedFilm);
        } catch (FilmNotFoundException e) {
            log.error("Фильм не найден для обновления: {}", film.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilm(@PathVariable Integer id) {
        try {
            filmService.deleteFilm(id);
            return ResponseEntity.noContent().build();
        } catch (FilmNotFoundException e) {
            log.error("Фильм не найден для удаления: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Object> addLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        ResponseEntity<Object> userCheck = checkUserExists(userId);
        if (userCheck != null) return userCheck;

        ResponseEntity<Object> filmCheck = checkFilmExists(filmId);
        if (filmCheck != null) return filmCheck;

        filmService.addLike(filmId, userId);
        Film updatedFilm = filmService.getFilm(filmId);
        return ResponseEntity.ok(updatedFilm);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Object> removeLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        ResponseEntity<Object> userCheck = checkUserExists(userId);
        if (userCheck != null) return userCheck;

        ResponseEntity<Object> filmCheck = checkFilmExists(filmId);
        if (filmCheck != null) return filmCheck;

        filmService.removeLike(filmId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/popular")
    public ResponseEntity<Object> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        if (count <= 0) {
            log.error("Некорректное значение count для популярных фильмов: {}", count);
            return ResponseEntity.badRequest().body(Map.of("error", "Count должен быть положительным числом"));
        }
        List<Film> popularFilms = filmService.getTopFilms(count);
        return ResponseEntity.ok(popularFilms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getFilmById(@PathVariable Integer id) {
        try {
            Film film = filmService.getFilm(id);
            return ResponseEntity.ok(film);
        } catch (FilmNotFoundException e) {
            log.error("Фильм с ID {} не найден", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // Вспомогательные методы проверки

    private ResponseEntity<Object> checkUserExists(Integer userId) {
        if (!userService.userExists(userId)) {
            log.error("Пользователь с ID {} не найден", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Пользователь с ID " + userId + " не найден"));
        }
        return null;
    }

    private ResponseEntity<Object> checkFilmExists(Integer filmId) {
        if (!filmService.filmExists(filmId)) {
            log.error("Фильм с ID {} не найден", filmId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Фильм с ID " + filmId + " не найден"));
        }
        return null;
    }
}