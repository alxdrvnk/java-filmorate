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
import java.util.Optional;

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
    public Optional<Director> getDirectorById(Integer id) {
        String query = "SELECT * FROM DIRECTORS WHERE ID=?";
        try {
            Optional<Director> director = Optional.ofNullable(jdbcTemplate.queryForObject(query, new DirectorMapper(), id));
            return director.stream().findAny();
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
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
            throw new FilmorateNotFoundException("Director with id = " + director.getId() + " not update");
        } else {
            return director;
        }
    }

    @Override
    public void deleteDirectorById(Integer id) {
        String sql1 = "DELETE FROM DIRECTORS WHERE ID = ?";
        Integer directorCount = jdbcTemplate.update(sql1, id);
        if (directorCount < 1) {
            throw new FilmorateNotFoundException("Director with id = " + id + " not found");
        }
    }

    private Map<String, Object> directorToParameters(Director director) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", director.getId());
        parameters.put("name", director.getName());
        return parameters;
    }
}
