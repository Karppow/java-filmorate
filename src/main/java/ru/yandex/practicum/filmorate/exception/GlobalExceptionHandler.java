package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        List<String> errorMessages = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        return new ResponseEntity<>(new ErrorResponse("Ошибки валидации: " + String.join(", ", errorMessages)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        return new ResponseEntity<>(new ErrorResponse("Ошибка: " + e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FilmNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFilmNotFoundException(FilmNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse("Ошибка: " + e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LikeAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleLikeAlreadyExistsException(LikeAlreadyExistsException e) {
        return new ResponseEntity<>(new ErrorResponse("Ошибка: " + e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LikeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLikeNotFoundException(LikeNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse("Ошибка: " + e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse("Ошибка: " + e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FutureBirthdayException.class)
    public ResponseEntity<ErrorResponse> handleFutureBirthdayException(FutureBirthdayException e) {
        return new ResponseEntity<>(new ErrorResponse("Ошибка: " + e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
