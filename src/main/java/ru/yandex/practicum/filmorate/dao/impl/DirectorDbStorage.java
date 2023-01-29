package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
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
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet("SELECT * FROM DIRECTORS WHERE ID=?", id);
        if (directorRows.next()) {
            return new Director(directorRows.getInt("ID"), directorRows.getString("NAME"));
        } else {
            throw new FilmorateNotFoundException("Ошибочный запрос, режиссёр отсутствует");
        }
    }

    @Override
    public Director createDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("DIRECTORS")
                .usingGeneratedKeyColumns("ID");
        int directorId = simpleJdbcInsert.executeAndReturnKey(directorToParameters(director)).intValue();
        return director.withId(directorId);
    }
    private Map<String, Object> directorToParameters(Director director){
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", director.getId());
        parameters.put("name", director.getName());
        return parameters;
    }

    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE DIRECTORS SET NAME = ? WHERE ID = ?";
        int updatedRow = jdbcTemplate.update(sql, director.getName(), director.getId());
        if (updatedRow == 0) {
            throw new FilmorateNotFoundException(String.format("Директор с id: %d не найден.", director.getId()));
        }
        return director;
    }

    @Override
    public void deleteDirectorById(Integer id) {
        String sql = "DELETE FROM DIRECTORS WHERE ID = ?";
        jdbcTemplate.update(sql, id);
    }
}
