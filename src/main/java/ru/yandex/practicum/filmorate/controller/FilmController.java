package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.LikeRequest;
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
        Film createdFilm = filmService.addFilm(film);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    @GetMapping
    public ResponseEntity<Film> getFilm(@RequestBody Film film) {
        if (film.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Film foundFilm = filmService.getFilm(film.getId());
        if (foundFilm == null) {
            throw new FilmNotFoundException(film.getId());
        }
        return ResponseEntity.ok(foundFilm);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        if (film.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Film updatedFilm = filmService.updateFilm(film);
        return ResponseEntity.ok(updatedFilm);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFilm(@RequestBody Film film) {
        if (film.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        filmService.deleteFilm(film.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/like")
    public ResponseEntity<Void> addLike(@RequestBody LikeRequest likeRequest) {
        filmService.addLike(likeRequest.getFilmId(), likeRequest.getUserId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/like")
    public ResponseEntity<Void> removeLike(@RequestBody LikeRequest likeRequest) {
        filmService.removeLike(likeRequest.getFilmId(), likeRequest.getUserId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        List<Film> popularFilms = filmService.getTopFilms(count);
        return ResponseEntity.ok(popularFilms);
    }
}
