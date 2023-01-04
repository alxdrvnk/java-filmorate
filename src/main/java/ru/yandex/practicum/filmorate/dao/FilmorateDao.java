package ru.yandex.practicum.filmorate.dao;

import java.util.List;
import java.util.Optional;

public interface FilmorateDao<T> {

    Long create(T t);

    Optional<T> getBy(Long id);

    List<T> getAll();

    void update(T t);

    void deleteBy(Long id);

}
