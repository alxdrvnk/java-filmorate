package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmDirectorDao {

    void createFilmDirectors(Film film);
    void updateFilmDirectors(Film film);
}
