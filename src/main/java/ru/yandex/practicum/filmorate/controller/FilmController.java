package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private List<Film> films = new ArrayList<>();

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        films.add(film);
        return film;
    }

    @PutMapping("/{id}")
    public Film updateFilm(@PathVariable Long id, @RequestBody Film film) {
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId().equals(id)) {
                films.set(i, film);
                return film;
            }
        }
        return null;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return films;
    }
}
