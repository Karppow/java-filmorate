package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

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
        try {
            userValidator.validate(user);
            fail("Ожидается ValidationException");
        } catch (ValidationException e) {
            assertEquals("Электронная почта не может быть пустой и должна содержать символ @", e.getMessage());
        }
    }

    @Test
    public void testUserValidationLogin() {
        User user = new User();
        user.setLogin("");
        user.setEmail("test@example.com"); // добавьте электронную почту
        try {
            userValidator.validate(user);
            fail("Ожидается ValidationException");
        } catch (ValidationException e) {
            assertEquals("Логин не может быть пустым и содержать пробелы", e.getMessage());
        }
    }

    @Test
    public void testUserValidationBirthday() {
        User user = new User();
        user.setBirthday(new Date(System.currentTimeMillis() + 86400000)); // завтрашняя дата
        user.setEmail("test@example.com"); // добавьте электронную почту
        user.setLogin("test"); // добавьте логин
        try {
            userValidator.validate(user);
            fail("Ожидается ValidationException");
        } catch (ValidationException e) {
            assertEquals("Дата рождения не может быть в будущем", e.getMessage());
        }
    }
}