package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorDao {
    List<Director> getAllDirectors();

    Director getDirectorById(Integer id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirectorById(Integer id);
}
