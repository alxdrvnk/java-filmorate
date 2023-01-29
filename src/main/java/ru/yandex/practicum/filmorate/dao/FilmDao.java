package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmDao extends FilmorateDao<Film> {
    List<Film> getPopularFilms(int count);

    List<Film> getByIds(Collection<Long> filmIds);
}
