package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    public void deleteBy(Long id) {
        String query = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public List<Film> getAll() {
        String query =
                "select f.*, m.name AS mpa_name FROM films AS f " +
                        "JOIN mpa AS m On f.mpa_id = m.id";
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
    public List<Film> getPopular(int count) {
        return null;
    }
    private Map<String, Object> filmToParameters(Film film) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", film.getId());
        parameters.put("title", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", film.getReleaseDate());
        parameters.put("duration", film.getDuration());
        parameters.put("map_id", film.getMpa().getId());
        return parameters;
    }

    private static final class FilmMapper implements RowMapper<Film> {

        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Film.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("title"))
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .duration(rs.getInt("duration"))
                    .mpa(makeMpa(rs))
                    .build();
        }

        private Mpa makeMpa(ResultSet rs) throws SQLException {
            return Mpa.builder()
                    .id(rs.getLong("mpa_id"))
                    .name(rs.getString("mpa_name")).build();
        }
    }
}