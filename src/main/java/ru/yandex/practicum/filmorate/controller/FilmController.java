package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validator.ErrorResponse;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final FilmValidator filmValidator;

    @Autowired
    public FilmController(FilmService filmService, FilmValidator filmValidator) {
        this.filmService = filmService;
        this.filmValidator = filmValidator;
    }

    @PostMapping
    public ResponseEntity<?> createFilm(@RequestBody Film film) {
        try {
            // Валидируем фильм
            filmValidator.validate(film);

            // Создаем фильм
            Film createdFilm = filmService.addFilm(film);

            // Возвращаем созданный фильм с кодом 201
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
        } catch (ValidationException e) {
            // Возвращаем 400 Bad Request с ошибкой валидации
            ErrorResponse errorResponse = new ErrorResponse("Ошибка валидации: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        try {
            List<Film> films = filmService.getAllFilms();
            if (films.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Возвращаем статус 204, если фильмы пустые
            }
            return ResponseEntity.ok(films);  // Возвращаем фильмы с кодом 200
        } catch (Exception e) {
            log.error("Произошла ошибка при получении списка фильмов", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);  // Ошибка 500
        }
    }

    @PutMapping
    public ResponseEntity<?> updateFilm(@RequestBody Film film) {
        try {
            // Логирование для отладки
            log.info("Запрос на обновление фильма: " + film);

            // Если ID фильма не указан
            if (film.getId() == null) {
                log.error("ID фильма не может быть null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("ID фильма не может быть null"));
            }

            // Обновляем фильм
            Film updatedFilm = filmService.updateFilm(film);

            // Возвращаем обновленный фильм
            return ResponseEntity.ok(updatedFilm);
        } catch (FilmNotFoundException e) {
            // Если фильм не найден
            log.error("Ошибка: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            // Логирование ошибки и возврат ответа 500
            log.error("Произошла ошибка при обновлении фильма", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Произошла ошибка при обновлении фильма"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilm(@PathVariable Long id) {
        filmService.deleteFilm(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable Long filmId,
                                        @PathVariable Long userId) {
        try {
            filmService.addLike(filmId, userId);
            Film updatedFilm = filmService.getFilm(filmId); // Получаем обновлённый фильм
            return ResponseEntity.status(HttpStatus.OK).body(updatedFilm); // Возвращаем фильм с лайками
        } catch (FilmNotFoundException e) {
            log.error("Ошибка: Фильм с ID " + filmId + " не найден", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 404 Not Found
        }
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable Long filmId,
                                           @PathVariable Long userId) {
        filmService.removeLike(filmId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        List<Film> popularFilms = filmService.getTopFilms(count);
        return ResponseEntity.ok(popularFilms);
    }
}

