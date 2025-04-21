package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.Exception.LikeAlreadyExistsException;
import ru.yandex.practicum.filmorate.Exception.LikeNotFoundException;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validator.ErrorResponse;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collections;
import java.util.List;

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
        this.userService = userService; // Инициализируем userService
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
        List<Film> films = filmService.getAllFilms();
        return films.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(films);
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
    public ResponseEntity<Void> deleteFilm(@PathVariable Integer id) {
        filmService.deleteFilm(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{filmId}/like/{userId}")
    public ResponseEntity<?> addLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        try {
            if (!userService.userExists(userId)) {
                log.error("Ошибка: Пользователь с ID " + userId + " не найден");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Пользователь не найден")); // 404 Not Found
            }

            if (!filmService.filmExists(filmId)) {
                log.error("Ошибка: Фильм с ID " + filmId + " не найден");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Фильм не найден")); // 404 Not Found
            }

            filmService.addLike(filmId, userId);
            Film updatedFilm = filmService.getFilm(filmId);
            return ResponseEntity.status(HttpStatus.OK).body(updatedFilm); // 200 OK
        } catch (RuntimeException e) {
            log.error("Ошибка: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Внутренняя ошибка сервера")); // 500 Internal Server Error
        }
    }


    @DeleteMapping("/{filmId}/like/{userId}")
    public ResponseEntity<ErrorResponse> removeLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        try {
            filmService.removeLike(filmId, userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
        } catch (FilmNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Фильм не найден: " + e.getMessage())); // 404 Not Found
        } catch (LikeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Лайк не найден: " + e.getMessage())); // 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Произошла ошибка: " + e.getMessage())); // 500 Internal Server Error
        }
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

