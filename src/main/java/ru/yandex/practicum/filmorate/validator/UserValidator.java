package ru.yandex.practicum.filmorate.validator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Date;

@Component
public class UserValidator {
    public void validate(User user) {
        // Проверка электронной почты
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        // Проверка логина
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        // Проверка имени
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new ValidationException("Имя не может быть пустым");
        }

        // Проверка даты рождения
        if (user.getBirthday() != null && user.getBirthday().after(new Date())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
