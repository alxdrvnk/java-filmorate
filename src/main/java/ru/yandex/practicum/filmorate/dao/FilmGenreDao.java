package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenreDao {

    List<Genre> getFilmGenres(Long filmId);

    void updateFilmGenres(Long filmId, List<Genre> genreIds);
}
