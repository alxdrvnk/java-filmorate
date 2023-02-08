package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface FilmGenreDao {

    List<Genre> getFilmGenres(Long filmId);

    void updateFilmGenres(Long filmId, Set<Genre> genreIds);
}
