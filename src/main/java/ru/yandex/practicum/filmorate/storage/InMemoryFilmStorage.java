package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long currentId = 1;

    @Override
    public Film addFilm(Film film) {
        film.setId(currentId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        }
        throw new FilmNotFoundException(film.getId()); // Используем кастомное исключение
    }

    @Override
    public void deleteFilm(long id) {
        films.remove(id);
    }

    @Override
    public Film getFilm(long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new FilmNotFoundException(id);  // Если фильм не найден, выбрасываем исключение
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }
}
