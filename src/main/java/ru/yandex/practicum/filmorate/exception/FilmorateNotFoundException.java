package ru.yandex.practicum.filmorate.exception;

public class FilmorateNotFoundException extends RuntimeException{
    public FilmorateNotFoundException(String message) {
        super(message);
    }
}
