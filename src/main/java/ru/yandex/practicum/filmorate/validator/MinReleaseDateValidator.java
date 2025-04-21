package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.service.MinReleaseDate;

import java.time.LocalDate;

public class MinReleaseDateValidator implements ConstraintValidator<MinReleaseDate, LocalDate> {

    private static final LocalDate MIN_DATE = LocalDate.of(1900, 1, 1);

    @Override
    public void initialize(MinReleaseDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isBefore(MIN_DATE);
    }
}