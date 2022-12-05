package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmorateValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilmService {

    private static final LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);

    private final Map<Integer, Film> films = new HashMap<>();

    private static Integer id = 1;

    private static Integer getNextId() {
        return id++;
    }

    public Film create(Film film) {
        validateReleaseDate(film);
        int id = getNextId();
        films.put(id, film.withId(id));
        return film;
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
