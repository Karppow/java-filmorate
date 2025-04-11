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

    @GetMapping
    public ResponseEntity<Film> getFilm(@RequestBody Film film) {
        if (film.getId() == null) {
            log.warn("Film ID must not be null for retrieval");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        log.info("Getting film with ID {}", film.getId());
        Film foundFilm = filmService.getFilm(film.getId());
        if (foundFilm == null) {
            log.warn("Film with ID {} not found", film.getId());
            throw new FilmNotFoundException(film.getId());
        }
        log.info("Film found: {}", foundFilm);
        return ResponseEntity.ok(foundFilm);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        if (film.getId() == null) {
            log.warn("Film ID must not be null for update");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        log.info("Updating film with ID {}", film.getId());
        Film updatedFilm = filmService.updateFilm(film);
        log.info("Film updated: {}", updatedFilm);
        return ResponseEntity.ok(updatedFilm);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFilm(@RequestBody Film film) {
        if (film.getId() == null) {
            log.warn("Film ID must not be null for deletion");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        log.info("Deleting film with ID {}", film.getId());
        filmService.deleteFilm(film.getId());
        log.info("Film with ID {} deleted", film.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/like")
    public ResponseEntity<Void> addLike(@RequestBody LikeRequest likeRequest) {
        log.info("User  with ID {} liked film with ID {}", likeRequest.getUserId(), likeRequest.getFilmId());
        filmService.addLike(likeRequest.getFilmId(), likeRequest.getUserId());
        log.info("Like added to film with ID {} by user with ID {}", likeRequest.getFilmId(), likeRequest.getUserId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/like")
    public ResponseEntity<Void> removeLike(@RequestBody LikeRequest likeRequest) {
        log.info("User  with ID {} removed like from film with ID {}", likeRequest.getUserId(), likeRequest.getFilmId());
        filmService.removeLike(likeRequest.getFilmId(), likeRequest.getUserId());
        log.info("Like removed from film with ID {} by user with ID {}", likeRequest.getFilmId(), likeRequest.getUserId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        log.info("Getting top {} popular films", count);
        List<Film> popularFilms = filmService.getTopFilms(count);
        log.info("Found {} popular films", popularFilms.size());
        return ResponseEntity.ok(popularFilms);
    }
    public static class LikeRequest {
        private Long filmId;
        private Long userId;

        // Геттер для filmId
        public Long getFilmId() {
            return filmId;
        }

        // Сеттер для filmId
        public void setFilmId(Long filmId) {
            this.filmId = filmId;
        }

        // Геттер для userId
        public Long getUserId() {
            return userId;
        }

        // Сеттер для userId
        public void setUserId(Long userId) {
            this.userId = userId;
        }
    }

}
