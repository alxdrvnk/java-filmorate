package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmLikeDao {

    void addFilmLike(Long filmId, Long userId);

    void removeFilmLike(Long filmId, Long userId);

    List<Film> getPopularFilms(int count);
}
