package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
        // Валидируем фильм
        filmValidator.validate(film);

        // Создаем фильм
        Film createdFilm = filmService.addFilm(film);

        // Возвращаем созданный фильм с кодом 201
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        List<Film> films = filmService.getAllFilms();
        return films.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(films);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        log.info("Запрос на обновление фильма: " + film);

        // Если ID фильма не указан
        if (film.getId() == null) {
            log.error("ID фильма не может быть null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Обновляем фильм
        Film updatedFilm = filmService.updateFilm(film);

        // Возвращаем обновленный фильм
        return ResponseEntity.ok(updatedFilm);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilm(@PathVariable Integer id) {
        filmService.deleteFilm(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Object> addLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        if (!userService.userExists(userId)) {
            log.error("Ошибка: Пользователь с ID " + userId + " не найден");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Пользователь с ID " + userId + " не найден")); // Возвращаем JSON-объект
        }

        if (!filmService.filmExists(filmId)) {
            log.error("Ошибка: Фильм с ID " + filmId + " не найден");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Фильм с ID " + filmId + " не найден")); // Возвращаем JSON-объект
        }

        filmService.addLike(filmId, userId);
        Film updatedFilm = filmService.getFilm(filmId);
        return ResponseEntity.ok(updatedFilm); // 200 OK
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        filmService.removeLike(filmId, userId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        if (count <= 0) {
            return ResponseEntity.badRequest().body(null);
        }
        List<Film> popularFilms = filmService.getTopFilms(count);
        return ResponseEntity.ok(popularFilms);
    }
}
