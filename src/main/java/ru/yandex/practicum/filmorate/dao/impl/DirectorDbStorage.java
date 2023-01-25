package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String query = "SELECT * FROM DIRECTORS WHERE ID=?";
        try {
            Director director = jdbcTemplate.queryForObject(query, new DirectorMapper(), id);
            return director;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Director createDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("DIRECTORS")
                .usingGeneratedKeyColumns("ID");
        Integer directorId = simpleJdbcInsert.executeAndReturnKey(directorToParameters(director)).intValue();
        return director.withId(directorId);
    }


    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE DIRECTORS SET NAME = ? WHERE ID = ?";
        Integer rowCount = jdbcTemplate.update(sql, director.getName(), director.getId());
        if (rowCount < 1) {
            return null;
        } else {
            return director;
        }
    }

    @Override
    public void deleteDirectorById(Integer id) {
        String sql = "DELETE FROM FILM_DIRECTORS WHERE DIRECTOR_ID = ?";
        Integer dirCount = jdbcTemplate.update(sql, id);
        String sql1 = "DELETE FROM DIRECTORS WHERE ID = ?";
        Integer directorCount = jdbcTemplate.update(sql1, id);
        if ((dirCount < 1) || (directorCount < 1)) {
            throw new FilmorateNotFoundException("Режисер с id = " + id + " не найден");
        }
    }

    private Map<String, Object> directorToParameters(Director director) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", director.getId());
        parameters.put("name", director.getName());
        return parameters;
    }
}
