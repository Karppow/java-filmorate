package ru.yandex.practicum.filmorate.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

  // Обработчик для ошибок валидации (400)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException e) {
    // Составляем список сообщений об ошибках для всех полей
    List<String> errorMessages = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());

    // Возвращаем статус 400 (BAD REQUEST) и список ошибок
    return new ResponseEntity<>("Ошибки валидации: " + String.join(", ", errorMessages), HttpStatus.BAD_REQUEST);
  }

  // Обработчик для ресурса не найден (404)
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException e) {
    // Возвращаем статус 404 (NOT FOUND) для случая, когда ресурс не найден
    return new ResponseEntity<>("Ресурс не найден: " + e.getMessage(), HttpStatus.NOT_FOUND);
  }

  // Обработчик для всех остальных исключений (500)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    // Возвращаем статус 500 (INTERNAL SERVER ERROR) для всех других ошибок
    return new ResponseEntity<>("Произошла ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
