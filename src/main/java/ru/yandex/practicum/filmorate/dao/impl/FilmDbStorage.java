package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
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

    private static final String SELECT_FILMS = "SELECT f.*, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name, " +
            "d.id AS director_id, d.name AS director_name " +
            "FROM films AS f INNER JOIN mpa AS m ON m.id = f.mpa_id " +
            "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
            "LEFT JOIN genre AS g ON g.id = fg.genre_id " +
            "LEFT JOIN film_directors AS fd ON fd.film_id = f.id " +
            "LEFT JOIN directors d ON fd.director_id = d.id ";

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        long filmId = simpleJdbcInsert.executeAndReturnKey(filmToParameters(film)).longValue();
        return film.withId(filmId);
    }

    @Override
    public Film update(Film film) {
        String query = "UPDATE films SET title = ?, description = ?, release_date = ?, duration = ?, rate = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
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
        String query = SELECT_FILMS + "ORDER BY f.id";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query);
        return FilmMapper.makeFilmList(rowSet);
    }

    @Override
    public Optional<Film> getBy(Long id) {

        String query = SELECT_FILMS + "WHERE f.id = ?";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, id);
            return FilmMapper.makeFilmList(rowSet).stream().findAny();
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {

        String query = SELECT_FILMS + "ORDER BY f.rate DESC  LIMIT ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, count);

        return FilmMapper.makeFilmList(rowSet);
    }

    @Override
    public List<Film> findFilmsBy(String query, String where) {
        String sql = SELECT_FILMS + where + "ORDER BY f.rate DESC";
        NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
        SqlParameterSource namedParameter = new MapSqlParameterSource().addValue("query", query);
        System.out.println(sql);
        SqlRowSet rowSet = jdbc.queryForRowSet(sql, namedParameter);
        return FilmMapper.makeFilmList(rowSet);
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