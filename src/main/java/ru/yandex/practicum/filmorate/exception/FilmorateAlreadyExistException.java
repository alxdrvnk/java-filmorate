package ru.yandex.practicum.filmorate.exception;

public class FilmorateAlreadyExistException extends RuntimeException {
    public FilmorateAlreadyExistException(String message) {
        super(message);
    }
}
