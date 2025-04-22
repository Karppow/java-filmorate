package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.GlobalExceptionHandler;
import ru.yandex.practicum.filmorate.model.User;

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
        GlobalExceptionHandler exception = assertThrows(GlobalExceptionHandler.class, () -> userValidator.validate(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    public void testUserValidationLogin() {
        User user = new User();
        user.setLogin("");
        user.setEmail("test@example.com"); // добавьте электронную почту
        GlobalExceptionHandler exception = assertThrows(GlobalExceptionHandler.class, () -> userValidator.validate(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }
}