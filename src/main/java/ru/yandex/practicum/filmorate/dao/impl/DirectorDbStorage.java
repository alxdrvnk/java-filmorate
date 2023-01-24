package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
@Repository
public class DirectorDbStorage implements DirectorDao {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Director> getAllDirectors() {
        return null;
    }

    @Override
    public Director getDirectorById(Integer id) {
        return null;
    }

    @Override
    public Director createDirector(Director director) {
        return null;
    }

    @Override
    public Director updateDirector(Director director) {
        return null;
    }

    @Override
    public void deleteDirectorById(Integer id) {

    }
}
