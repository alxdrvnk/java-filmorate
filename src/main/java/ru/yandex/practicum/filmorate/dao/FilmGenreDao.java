package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenreDao {

    List<Genre> getFilmGenres(Long filmId);

    void addFilmGenres(Long filmId, List<Genre> genreIds);

    void removeFilmGenre(Long filmId, Long genreId);

}
