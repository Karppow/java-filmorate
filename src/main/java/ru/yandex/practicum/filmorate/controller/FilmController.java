package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private final FilmValidator filmValidator = new FilmValidator();

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        filmValidator.validate(film);
        log.info("Creating film: {}", film);
        long id = films.size() + 1;
        film.setId(id);
        films.put(id, film);
        return ResponseEntity.status(HttpStatus.CREATED).body(film);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        filmValidator.validate(film);
        log.info("Updating film: {}", film);

        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Film with ID {} updated successfully", film.getId());
            return ResponseEntity.ok(film);
        } else {
            log.warn("Film with ID {} not found. Existing films: {}", film.getId(), films.keySet());
            throw new FilmNotFoundException(film.getId());
        }
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        log.info("Getting all films");
        return ResponseEntity.ok(List.copyOf(films.values()));
    }
}
