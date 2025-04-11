package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        log.info("Creating film: {}", film);
        Film createdFilm = filmService.addFilm(film);
        log.info("Film created with ID: {}", createdFilm.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable Long id) {
        log.info("Getting film with ID {}", id);
        Film film = filmService.getFilm(id);
        if (film == null) {
            log.warn("Film with ID {} not found", id);
            throw new FilmNotFoundException(id);
        }
        log.info("Film found: {}", film);
        return ResponseEntity.ok(film);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Film> updateFilm(@PathVariable Long id, @RequestBody Film film) {
        film.setId(id);
        log.info("Updating film with ID {}", id);
        Film updatedFilm = filmService.updateFilm(film);
        log.info("Film updated: {}", updatedFilm);
        return ResponseEntity.ok(updatedFilm);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilm(@PathVariable Long id) {
        log.info("Deleting film with ID {}", id);
        filmService.deleteFilm(id);
        log.info("Film with ID {} deleted", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("User  with ID {} liked film with ID {}", userId, id);
        filmService.addLike(id, userId);
        log.info("Like added to film with ID {} by user with ID {}", id, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("User  with ID {} removed like from film with ID {}", userId, id);
        filmService.removeLike(id, userId);
        log.info("Like removed from film with ID {} by user with ID {}", id, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        log.info("Getting top {} popular films", count);
        List<Film> popularFilms = filmService.getTopFilms(count);
        log.info("Found {} popular films", popularFilms.size());
        return ResponseEntity.ok(popularFilms);
    }
}
