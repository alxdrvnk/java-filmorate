package ru.yandex.practicum.filmorate.exception;

public class FilmorateValidationException extends RuntimeException {
    public FilmorateValidationException(String message){
        super(message);
    }
}
