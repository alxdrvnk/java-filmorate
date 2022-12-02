package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class FilmorateExceptionHandler extends ResponseEntityExceptionHandler {

    //TODO: Create ValidationException
    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<Object> handlerValidationException(RuntimeException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    //TODO: Create NotFoundException
    @ExceptionHandler(value = RuntimeException.class)
    protected ResponseEntity<Object> handlerNotFoundException(RuntimeException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    // TODO: Create AlreadyExistException
    @ExceptionHandler(value = RuntimeException.class)
    protected ResponseEntity<Object> handlerAlreadyExistException(RuntimeException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
