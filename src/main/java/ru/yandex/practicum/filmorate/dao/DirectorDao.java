package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorDao {
    List<Director> getAllDirectors();

    Optional<Director> getDirectorById(Integer id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirectorById(Integer id);
}
