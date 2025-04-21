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

    // Конструктор без инициализации friends
    public User() {
        // friends инициализируется позже
    }

    public Set<Integer> getFriends() {
        if (friends == null) {
            friends = new HashSet<>();  // Инициализация только при обращении
        }
        return friends;
    }

    public void addFriend(Integer friendId) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        this.friends.add(friendId);
    }
}
