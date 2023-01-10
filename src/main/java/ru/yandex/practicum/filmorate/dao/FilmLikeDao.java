package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmLikeDao {

    void addFilmLike(Long filmId, Long userId);

    void removeFilmLike(Long filmId, Long userId);

    Optional<Integer>getFilmLikesCount(Long filmId);

    List<Film> getPopularFilms(int count);
}
