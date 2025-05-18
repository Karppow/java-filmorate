package ru.yandex.practicum.filmorate.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Primary
@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        int filmId = keyHolder.getKey().intValue();
        film.setId(filmId);
        addGenresToFilm(filmId, film.getGenres());

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        // Удаляем старые жанры
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        // Добавляем новые без дубликатов
        addGenresToFilm(film.getId(), film.getGenres());

        return film;
    }

    @Override
    public void deleteFilm(Integer id) {
        String checkSql = "SELECT COUNT(*) FROM films WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, id);

        if (count == 0) {
            throw new RuntimeException("Film with id " + id + " not found");
        }

        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Film getFilm(Integer id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        Film film = jdbcTemplate.queryForObject(sql, new FilmRowMapper(), id);

        String genreSql = "SELECT g.id, g.name FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(genreSql, new GenreRowMapper(), id);

// Фильтрация дубликатов
        Set<Genre> uniqueGenres = new LinkedHashSet<>(genres);
        film.setGenres(new ArrayList<>(uniqueGenres));

        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper());

        for (Film film : films) {
            String genreSql = "SELECT g.id, g.name FROM genres g " +
                    "JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
            List<Genre> genres = jdbcTemplate.query(genreSql, new GenreRowMapper(), film.getId());

            Set<Genre> uniqueGenres = new LinkedHashSet<>(genres);
            film.setGenres(new ArrayList<>(uniqueGenres));
        }

        return films;
    }

    // Сделали НЕ static, чтобы можно было вызывать нестатические методы класса
    private class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            int mpaId = rs.getInt("mpa_id");
            film.setMpa(new Mpa(mpaId, getMpaName(mpaId)));
            return film;
        }
    }

    private void addGenresToFilm(int filmId, List<Genre> genres) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        // Уникальные жанры по id
        Set<Integer> uniqueGenreIds = new HashSet<>();
        for (Genre genre : genres) {
            if (uniqueGenreIds.add(genre.getId())) { // true, если добавлен впервые
                jdbcTemplate.update(sql, filmId, genre.getId());
            }
        }
    }

    private static class GenreRowMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Genre(rs.getInt("id"), rs.getString("name"));
        }
    }

    public void addLike(int filmId, int userId) {
        jdbcTemplate.update("INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        jdbcTemplate.update("DELETE FROM film_likes WHERE film_id = ? AND user_id = ?", filmId, userId);
    }

    public List<Integer> getLikesForFilm(int filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, filmId);
    }

    private String getMpaName(int id) {
        String sql = "SELECT name FROM mpa WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, id);
    }
}