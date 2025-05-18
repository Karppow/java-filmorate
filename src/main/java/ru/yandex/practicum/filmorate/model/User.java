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

    private Set<Integer> friends = new HashSet<>();

    @NotBlank
    @Email(message = "Некорректный формат адреса электронной почты")
    private String email;

    @NotBlank(message = "Логин не может быть пустым и не должен содержать пробелы")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Логин может содержать только буквы и цифры")
    private String login;

    @PastOrPresent(message = "Дата рождения должна быть сегодняшней или прошедшей")
    private LocalDate birthday;

    public void addFriend(Integer friendId) {
        if (friendId != null && !friendId.equals(this.id)) {
            friends.add(friendId);
        }
    }

    public void removeFriend(Integer friendId) {
        friends.remove(friendId);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', friends=" + friends + "}";
    }
}