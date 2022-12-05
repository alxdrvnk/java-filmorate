package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.yandex.practicum.filmorate.error.FilmorateError;
import ru.yandex.practicum.filmorate.exception.FilmorateAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmorateValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class FilmorateExceptionHandler {
    @ExceptionHandler(value = FilmorateValidationException.class)
    public ResponseEntity<Object> handleValidationException(FilmorateValidationException exception,
                                                            WebRequest request) {
        log.warn(String.format("Запрос %s завершился ошибкой: %s.", request.getDescription(false), exception.getMessage()));
        FilmorateError error = FilmorateError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errors(List.of(exception.getMessage())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(value = FilmorateNotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(FilmorateNotFoundException exception,
                                                          WebRequest request) {

        log.warn(String.format("Запрос %s завершился ошибкой: %s.", request.getDescription(false), exception.getMessage()));
        FilmorateError error = FilmorateError.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .errors(List.of(exception.getMessage())).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(value = FilmorateAlreadyExistException.class)
    public ResponseEntity<Object> handleAlreadyExistException(FilmorateAlreadyExistException exception,
                                                              WebRequest request) {
        log.warn(String.format("Запрос %s завершился ошибкой: %s.", request.getDescription(false), exception.getMessage()));
        FilmorateError error = FilmorateError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errors(List.of(exception.getMessage())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception,
                                                              WebRequest request) {
        List<String> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        log.warn(String.format("Запрос %s завершился ошибкой: %s.", request.getDescription(false), errors));
        FilmorateError error = FilmorateError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errors(errors).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}