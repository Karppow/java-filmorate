package ru.yandex.practicum.filmorate.validator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Date;

@Component
public class UserValidator {
    public void validate(User user) {
        StringBuilder errors = new StringBuilder();

        // Проверка электронной почты
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            errors.append("Электронная почта не может быть пустой и должна содержать символ @; ");
        }

        // Проверка логина
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            errors.append("Логин не может быть пустым и содержать пробелы; ");
        }

        // Проверка имени
        if (user.getName() == null || user.getName().isEmpty()) {
            errors.append("Имя не может быть пустым; ");
        }

        // Проверка даты рождения
        if (user.getBirthday() != null && user.getBirthday().after(new Date())) {
            errors.append("Дата рождения не может быть в будущем; ");
        }

        // Если есть ошибки, выбрасываем исключение
        if (errors.length() > 0) {
            throw new ValidationException(errors.toString());
        }
    }
}

