package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage storage;

    public Film create(Film film) {
        return storage.create(film);
    }

    public List<Film> getAllFilms() {
        return storage.getAllFilms();
    }

    public Film getFilmBy(Long id) {
        return storage.get(id);
    }

    public Film update(Film film) {
        return storage.update(film);
    }

    public Film setFilmLike(Long filmId, Long userId) {
        return storage.addLike(filmId, userId);
    }
}
