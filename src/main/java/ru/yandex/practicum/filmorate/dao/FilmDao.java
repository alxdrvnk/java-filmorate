package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.controller.dto.By;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao extends FilmorateDao<Film> {
    List<Film> getPopularFilms(int count);

    List<Film> findFilmsBy(String query, By by);



    void addDirectorForFilm(Film film);

    void deleteDirectorForFilm(Long filmId);

    List<Film> getDirectorFilmSortedByLike(int directorId);

    List<Film> getDirectorFilmSortedByYear(int directorId);
}
