package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

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
        String query = "SELECT f.*, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name, fd.DIRECTOR_ID, d.NAME AS DIRECTOR_NAME " +
                "FROM films AS f JOIN mpa AS m ON f.mpa_id = m.id " +
                "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
                "LEFT JOIN genre AS g ON g.id = fg.genre_id " +
                "LEFT JOIN FILM_DIRECTORS fd on f.ID = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS d on fd.DIRECTOR_ID = d.ID " +
                "ORDER BY f.id";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query);
        return FilmMapper.makeFilmList(rowSet);
    }

    @Override
    public Optional<Film> getBy(Long id) {
        String query = "SELECT f.*, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name, fd.DIRECTOR_ID, d.NAME AS DIRECTOR_NAME " +
                "FROM films AS f " +
                "INNER JOIN mpa AS m ON f.mpa_id = m.id " +
                "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
                "LEFT JOIN genre AS g ON g.id = fg.genre_id " +
                "LEFT JOIN FILM_DIRECTORS fd on f.ID = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS d on fd.DIRECTOR_ID = d.ID WHERE f.id = ?;";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, id);
            return FilmMapper.makeFilmList(rowSet).stream().findAny();
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String query = "SELECT f.*, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name, fd.DIRECTOR_ID, d.NAME AS DIRECTOR_NAME " +
                "FROM films AS f " +
                "INNER JOIN mpa AS m ON m.id = f.mpa_id " +
                "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
                "LEFT JOIN genre AS g ON g.id = fg.genre_id " +
                "LEFT JOIN FILM_DIRECTORS fd on f.ID = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS d on fd.DIRECTOR_ID = d.ID " +
                "RIGHT JOIN (SELECT id from films order by rate DESC LIMIT ?) as flm ON flm.id = f.id";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, count);

        return FilmMapper.makeFilmList(rowSet);
    }

    @Override
    public List<Film> getByIds(Collection<Long> filmIds) {
        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String query = String.format(
                "SELECT f.*, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name, fd.DIRECTOR_ID, d.NAME AS DIRECTOR_NAME " +
                        "FROM films AS f " +
                        "JOIN mpa AS m ON f.mpa_id = m.id " +
                        "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
                        "LEFT JOIN genre AS g ON g.id = fg.genre_id " +
                        "LEFT JOIN FILM_DIRECTORS fd on f.ID = fd.FILM_ID " +
                        "LEFT JOIN DIRECTORS d on fd.DIRECTOR_ID = d.ID " +
                        "WHERE f.id in (%s) " +
                        "ORDER BY f.id", inSql);

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, filmIds.toArray());
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

    @Override
    public void addDirectorForFilm(Film film) {
        if (film.getDirectors().size() != 0) {
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update("INSERT INTO FILM_DIRECTORS (DIRECTOR_ID, FILM_ID) VALUES ( ?, ? )", director.getId(), film.getId());
            }
        }
    }

    @Override
    public void deleteDirectorForFilm(Long filmId) {
        jdbcTemplate.update("DELETE FROM FILM_DIRECTORS WHERE FILM_ID = ?", filmId);
    }

    @Override
    public List<Film> getDirectorFilmSortedByLike(int directorId) {
        String sql = "SELECT f.*, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name, fd.DIRECTOR_ID, d.NAME AS DIRECTOR_NAME " +
                "FROM films AS f INNER JOIN mpa AS m ON m.id = f.mpa_id " +
                "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
                "LEFT JOIN genre AS g ON g.id = fg.genre_id " +
                "LEFT JOIN FILM_DIRECTORS fd on f.ID = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS d on fd.DIRECTOR_ID = d.ID " +
                "LEFT JOIN LIKES lk on f.ID = lk.FILM_ID" +
                " WHERE DIRECTOR_ID = ? " +
                "GROUP BY f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATE, f.MPA_ID, mpa_name, genre_id, genre_name,fd.DIRECTOR_ID,DIRECTOR_NAME " +
                "ORDER by COUNT(lk.USER_ID) DESC";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, directorId);
        return FilmMapper.makeFilmList(rowSet);
    }

    @Override
    public List<Film> getDirectorFilmSortedByYear(int directorId) {
        String sql = "SELECT f.*, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name, fd.DIRECTOR_ID, d.NAME AS DIRECTOR_NAME " +
                "FROM films AS f " +
                "INNER JOIN mpa AS m ON m.id = f.mpa_id " +
                "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
                "LEFT JOIN genre AS g ON g.id = fg.genre_id " +
                "LEFT JOIN FILM_DIRECTORS fd on f.ID = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS d on fd.DIRECTOR_ID = d.ID " +
                "LEFT JOIN LIKES lk on f.ID = lk.FILM_ID" +
                " WHERE DIRECTOR_ID = ? " +
                "GROUP BY f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATE, f.MPA_ID, mpa_name, genre_id, genre_name,fd.DIRECTOR_ID,DIRECTOR_NAME " +
                "ORDER by EXTRACT(YEAR FROM CAST(f.RELEASE_DATE AS date))";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, directorId);
        return FilmMapper.makeFilmList(rowSet);
    }
}
