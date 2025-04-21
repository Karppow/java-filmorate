package ru.yandex.practicum.filmorate.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;  // Исправленный импорт
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.validator.ErrorResponse;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
    List<String> errorMessages = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());
    return new ResponseEntity<>(new ErrorResponse("Ошибки валидации: " + String.join(", ", errorMessages)), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
    return new ResponseEntity<>(new ErrorResponse("Ресурс не найден: " + e.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(FilmNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleFilmNotFoundException(FilmNotFoundException e) {
    return new ResponseEntity<>(new ErrorResponse("Фильм не найден: " + e.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
    return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    return new ResponseEntity<>(new ErrorResponse("Произошла ошибка: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException e) {
    // Возвращаем статус и сообщение ошибки
    return new ResponseEntity<>(new ErrorResponse(e.getReason()), e.getStatusCode());
  }
}
