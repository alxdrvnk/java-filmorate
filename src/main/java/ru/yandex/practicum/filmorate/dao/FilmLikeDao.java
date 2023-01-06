package ru.yandex.practicum.filmorate.dao;

public interface FilmLikeDao {

    void addFilmLike(Long filmId, Long userId);

    void removeFilmLiek(Long filmId, Long userId);

    void getPopiarFilms(int count);
}
