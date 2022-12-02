package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilmService {
    private final Map<Integer, Film> films = new HashMap<>();

    private static Integer id = 0;

    private static Integer getNextId() {
        return id++;
    }

    public Film create(Film film) {
        int id = getNextId();
        film.setId(id);
        films.put(id, film);
        return film;
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Film getFilmBy(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new RuntimeException(String.format("Фильм с id: %d не найден.", id));
        }
    }

    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new RuntimeException(String.format("Фильм с id: %d не найден.", film.getId()));
        }
    }
}
