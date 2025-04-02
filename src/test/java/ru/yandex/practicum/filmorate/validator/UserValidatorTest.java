package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidatorTest {
    private UserValidator userValidator;

    @BeforeEach
    public void setUp() {
        userValidator = new UserValidator();
    }

    @Test
    public void testUserValidationEmail() {
        User user = new User();
        user.setEmail("");
        ValidationException exception = assertThrows(ValidationException.class, () -> userValidator.validate(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    public void testUserValidationLogin() {
        User user = new User();
        user.setLogin("");
        user.setEmail("test@example.com"); // добавьте электронную почту
        ValidationException exception = assertThrows(ValidationException.class, () -> userValidator.validate(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    public void testUserValidationBirthday() {
        User user = new User();
        user.setBirthday(new Date(System.currentTimeMillis() + 86400000)); // завтрашняя дата
        user.setEmail("test@example.com"); // добавьте электронную почту
        user.setLogin("test"); // добавьте логин
        ValidationException exception = assertThrows(ValidationException.class, () -> userValidator.validate(user));
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }
}