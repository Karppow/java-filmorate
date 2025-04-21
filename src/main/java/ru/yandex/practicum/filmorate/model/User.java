package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Integer id;
    private String name;
    private Set<Integer> friends;

    @Email
    private String email;

    @NotBlank(message = "Логин не может быть пустым и не должен содержать пробелы")
    @Pattern(regexp = "^[^\\s]+$", message = "Логин не должен содержать пробелы")
    private String login;

    @PastOrPresent
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
