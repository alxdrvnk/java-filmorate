package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface FilmorateDao<T> {

    T create(T t);

    Optional<T> getBy(Long id);

    List<T> getAll();

    T update(T t);

    int deleteBy(Long id);

}
