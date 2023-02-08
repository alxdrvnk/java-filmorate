package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.controller.dto.By;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmDao extends FilmorateDao<Film> {

    List<Film> findFilmsBy(String query, By by);

    List<Film> getPopularFilms(Integer count, Integer genreId, Integer year);

    void addDirectorForFilm(Film film);

    void deleteDirectorForFilm(Long filmId);

    List<Film> getDirectorFilmSortedByLike(int directorId);

    List<Film> getDirectorFilmSortedByYear(int directorId);

    boolean findIfUserLikedFilm(Long filmId, Long userId);

    List<Film> getByIds(Collection<Long> filmIds);

    List<Film> getCommonFilms(Long userId, Long friendId);

}
