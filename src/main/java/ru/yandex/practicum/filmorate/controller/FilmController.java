package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Logger logger = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();
    private final LocalDate targetDate = LocalDate.of(1895, 12, 28);

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        valid(film);
        logger.info("Creating film: {}", film);
        long id = films.size() + 1;
        film.setId(id);
        films.put(id, film); // Добавляем фильм в карту
        return ResponseEntity.status(HttpStatus.CREATED).body(film);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        valid(film);
        logger.info("Updating film: {}", film);
        Long id = film.getId(); // Получаем ID из объекта Film
        if (id != null && films.containsKey(id)) {
            films.put(id, film);
            return ResponseEntity.ok(film);
        } else {
            logger.warn("Film with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        logger.info("Getting all films");
        return ResponseEntity.ok(new ArrayList<>(films.values()));
    }

    private void valid(Film film) {
        if (film.getReleaseDate().isBefore(targetDate)) {
            throw new ValidationException("Дата выхода позже 1895");
        }
    }

}
