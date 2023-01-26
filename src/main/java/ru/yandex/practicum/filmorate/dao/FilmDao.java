package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao extends FilmorateDao<Film> {
    List<Film> getPopularFilms(int count);


    void addDirectorForFilm(Film film);

    void deleteDirectorForFilm(Film film);
}
