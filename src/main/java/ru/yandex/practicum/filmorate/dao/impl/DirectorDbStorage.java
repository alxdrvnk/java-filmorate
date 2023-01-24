package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
@Repository
public class DirectorDbStorage implements DirectorDao {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Director> getAllDirectors() {
        return jdbcTemplate.query("SELECT * FROM DIRECTORS;", new DirectorMapper());
    }

    @Override
    public Director getDirectorById(Integer id) {
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet("SELECT * FROM DIRECTORS WHERE ID=?", id);
        if (directorRows.next()) {
            return new Director(directorRows.getInt("ID"), directorRows.getString("NAME"));
        } else {
            throw new FilmorateNotFoundException("Ошибочный запрос, режиссёр отсутствует");
        }
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
