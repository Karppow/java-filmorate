package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.service.MinReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Long id;
    private Set<Long> likes = new HashSet<>();

    @NotNull(message = "Название не может быть пустым")
    private String name;

    @Size(max = 3000, message = "Максимальная длина описания - 3000 символов")
    private String description;

    @MinReleaseDate
    @PastOrPresent
    private LocalDate releaseDate;

    @Min(value = 1, message = "Длительность фильма должна быть больше нуля")
    private int duration;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Long> getLikes() {
        return likes;
    }

    public void setLikes(Set<Long> likes) {
        this.likes = likes;
    }
}
