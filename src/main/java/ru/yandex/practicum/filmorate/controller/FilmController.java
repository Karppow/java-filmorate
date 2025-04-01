package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private List<Film> films = new ArrayList<>();

    @PostMapping
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        films.add(film);
        return ResponseEntity.status(HttpStatus.CREATED).body(film);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Film> updateFilm(@PathVariable Long id, @RequestBody Film film) {
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId().equals(id)) {
                films.set(i, film);
                return ResponseEntity.ok(film);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        return ResponseEntity.ok(films);
    }
}