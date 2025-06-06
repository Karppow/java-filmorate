package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<List<Genre>> getAllGenres() {
        List<Genre> genres = genreService.getAllGenres();
        if (genres.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenre(@PathVariable Integer id) {
        Genre genre = genreService.getGenre(id);
        if (genre == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(genre);
    }
}

