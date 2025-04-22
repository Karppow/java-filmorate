package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Integer id;
    private Set<Integer> likes = new HashSet<>();

    @NotNull(message = "Название не может быть пустым")
    private String name;

    @Size(max = 100, message = "Максимальная длина описания - 100 символов")
    private String description;

    @PastOrPresent
    private LocalDate releaseDate;

    @Min(value = 1, message = "Длительность фильма должна быть больше нуля")
    private int duration;
}
