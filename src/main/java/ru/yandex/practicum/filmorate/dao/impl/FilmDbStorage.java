package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component("filmDBStorage")
@Primary
public class FilmDbStorage implements FilmDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        Long filmId = simpleJdbcInsert.executeAndReturnKey(filmToParameters(film)).longValue();
        return film.withId(filmId);
    }

    @Override
    public Film update(Film film) {
        String query = "UPDATE films SET title = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return film;
    }

    @Override
    public int deleteBy(Long id) {
        String query = "DELETE FROM films WHERE id = ?";
        return jdbcTemplate.update(query, id);
    }

    @Override
    public List<Film> getAll() {
        String query =
                "SELECT f.*, m.name AS mpa_name FROM films AS f " +
                        "JOIN mpa AS m On f.mpa_id = m.id " +
                        "ORDER BY f.id";
        return jdbcTemplate.query(query, new FilmMapper());
    }

    @Override
    public Optional<Film> getBy(Long id) {
        String query =
                "SELECT f.*, m.name AS mpa_name FROM films AS f " +
                        "INNER JOIN mpa AS m ON f.mpa_id = m.id " +
                        "WHERE f.id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, new FilmMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String query = "SELECT *, mpa.name AS mpa_name FROM films AS flm " +
                       "INNER JOIN mpa ON mpa.id = flm.mpa_id " +
                       "ORDER BY flm.rate DESC " +
                       "LIMIT ?";

        return jdbcTemplate.query(query, new FilmMapper(), count);
    }

    private Map<String, Object> filmToParameters(Film film) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", film.getId());
        parameters.put("title", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", film.getReleaseDate());
        parameters.put("duration", film.getDuration());
        parameters.put("rate", film.getRate());
        parameters.put("mpa_id", film.getMpa().getId());
        return parameters;
    }

}