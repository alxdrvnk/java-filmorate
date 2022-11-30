package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

@Service
public class FilmService {
    private final List<Film> films = new ArrayList<>();

    private static Integer id = 0;

    private static Integer getNextId() {
        return id++;
    }

    public Film create(Film film) {
        film.setId(getNextId());
        films.add(film);
        return film;
    }

    public List<Film> getAllFilms() {
        return films;
    }

    public Film getFilmBy(int id) {
        return films.get(id);
    }
}
