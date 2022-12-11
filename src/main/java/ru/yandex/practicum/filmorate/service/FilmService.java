package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmorateAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmorateValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage storage;

    public Film create(Film film) {
        return storage.create(film);
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Film getFilmBy(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new FilmorateNotFoundException(String.format("Фильм с id: %d не найден.", id));
        }
    }

    public Film update(Film film) {
        validateReleaseDate(film);

        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new FilmorateNotFoundException(String.format("Фильм с id: %d не найден.", film.getId()));
        }
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(cinemaBirthday)) {
            throw new FilmorateValidationException(
                    String.format("Дата релиза не может быть раньше чем %s", cinemaBirthday));
        }
    }
}
