package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Integer id;
    private String name;

    @JsonProperty(defaultValue = "[]")
    private Set<Integer> friends = new HashSet<>(); // Инициализация сразу

    @Email(message = "Некорректный формат адреса электронной почты")
    private String email;

    @NotBlank(message = "Логин не может быть пустым и не должен содержать пробелы")
    @Pattern(regexp = "^[A-Za-z0-9\\s]+$", message = "Логин может содержать только буквы, цифры и пробелы")
    private String login;

    @PastOrPresent(message = "Дата рождения должна быть сегодняшней или прошедшей")
    private LocalDate birthday;

    // Конструктор с инициализацией friends
    public User() {
        this.friends = new HashSet<>(); // Инициализация friends сразу
    }

    public Set<Integer> getFriends() {
        return friends; // friends всегда инициализирован
    }

    public void addFriend(Integer friendId) {
        this.friends.add(friendId); // friends уже инициализирован
    }

    public void removeFriend(Integer friendId) {
        this.friends.remove(friendId);
    }
}

