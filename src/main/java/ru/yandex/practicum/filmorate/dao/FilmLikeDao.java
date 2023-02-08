package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmLikeDao {

    boolean addFilmLike(Long filmId, Long userId);

    boolean removeFilmLike(Long filmId, Long userId);

    List<Film> getSameLikesByUser(Long userId, int count);

}
