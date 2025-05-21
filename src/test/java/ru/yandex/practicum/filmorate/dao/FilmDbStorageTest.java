package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") // профиль с настройками для тестов, например, H2
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmDbStorage;

    private Film testFilm;

    @BeforeEach
    void setup() {
        // Создаем тестовый фильм
        testFilm = new Film();
        testFilm.setName("Test Movie");
        testFilm.setDescription("Description");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(120);
        testFilm.setMpa(new Mpa(1, null)); // допустим, MPA с id=1 уже есть в БД
        testFilm.setGenres(List.of(new Genre(1, null), new Genre(2, null))); // допустим, жанры с id=1,2 есть в БД
    }

    @Test
    void addAndGetFilm() {
        Film addedFilm = filmDbStorage.addFilm(testFilm);
        assertThat(addedFilm.getId()).isPositive();

        Film retrievedFilm = filmDbStorage.getFilm(addedFilm.getId());
        assertThat(retrievedFilm).isNotNull();
        assertThat(retrievedFilm.getName()).isEqualTo(testFilm.getName());
        assertThat(retrievedFilm.getGenres()).extracting("id")
                .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void updateFilm() {
        Film addedFilm = filmDbStorage.addFilm(testFilm);
        addedFilm.setName("Updated Name");
        addedFilm.setGenres(List.of(new Genre(2, null))); // только один жанр

        Film updatedFilm = filmDbStorage.updateFilm(addedFilm);
        assertThat(updatedFilm.getName()).isEqualTo("Updated Name");

        Film retrievedFilm = filmDbStorage.getFilm(updatedFilm.getId());
        assertThat(retrievedFilm.getGenres()).extracting("id")
                .containsExactly(2);
    }

    @Test
    void deleteFilm() {
        Film addedFilm = filmDbStorage.addFilm(testFilm);
        int id = addedFilm.getId();

        filmDbStorage.deleteFilm(id);
        assertThatThrownBy(() -> filmDbStorage.getFilm(id))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    void addAndRemoveLike() {
        Film addedFilm = filmDbStorage.addFilm(testFilm);
        int filmId = addedFilm.getId();
        int userId = 1; // допустим, пользователь с id=1 есть

        filmDbStorage.addLike(filmId, userId);
        List<Integer> likes = filmDbStorage.getLikesForFilm(filmId);
        assertThat(likes).contains(userId);

        filmDbStorage.removeLike(filmId, userId);
        likes = filmDbStorage.getLikesForFilm(filmId);
        assertThat(likes).doesNotContain(userId);
    }

    @Test
    void getAllFilms() {
        filmDbStorage.addFilm(testFilm);
        List<Film> films = filmDbStorage.getAllFilms();
        assertThat(films).isNotEmpty();
    }
}
