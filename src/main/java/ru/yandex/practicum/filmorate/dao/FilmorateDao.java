package ru.yandex.practicum.filmorate.dao;

import java.util.List;
import java.util.Optional;

public interface FilmorateDao<T> {

    T create(T t);

    Optional<T> getBy(Long id);

    List<T> getAll();

    T update(T t);

    void deleteBy(Long id);

}
