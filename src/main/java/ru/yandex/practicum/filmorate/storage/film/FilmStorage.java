package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    List<Film> getAllFilms();

    Film get(Long id);

    List<Film> getPopular(int count);

    Film addLike(Long filmId, Long userId);

    Film removeLike(Long filmId, Long userId);

}
