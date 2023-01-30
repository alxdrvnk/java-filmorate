package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao extends FilmorateDao<Film> {
    List<Film> getPopularFilms(int count);
    
    void addDirectorForFilm(Film film);

    void deleteDirectorForFilm(Long filmId);

    List<Film> getDirectorFilmSortedByLike(int directorId);

    List<Film> getDirectorFilmSortedByYear(int directorId);

    boolean findIfUserLikedFilm(Long filmId, Long userId);
}
