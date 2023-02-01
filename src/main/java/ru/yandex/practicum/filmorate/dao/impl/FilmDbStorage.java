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
import ru.yandex.practicum.filmorate.controller.dto.By;
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
    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        String genreIdFilter =
                "INNER JOIN (" +
                        "SELECT film_id FROM film_genres " +
                        String.format("WHERE genre_id = %d ", genreId) +
                        ") AS flmgnr ON flmgnr.film_id = f.id";
        String query = "SELECT f.*, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name, fd.DIRECTOR_ID, d.NAME AS DIRECTOR_NAME " +
                "FROM films AS f " +
                "INNER JOIN mpa AS m ON m.id = f.mpa_id " +
                "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
                "LEFT JOIN genre AS g ON g.id = fg.genre_id " +
                "LEFT JOIN FILM_DIRECTORS fd on f.ID = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS d on fd.DIRECTOR_ID = d.ID " +
                "RIGHT JOIN (SELECT id from films " +
                (year != null ?
                        String.format("WHERE EXTRACT(YEAR FROM release_date) = %d ", year) : "") +
                "ORDER BY rate DESC LIMIT ?) AS flm ON flm.id = f.id " +
                (genreId != null ?
                        genreIdFilter : "");
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, count);
        return new ArrayList<>(FilmMapper.makeFilmList(rowSet));
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

    @Override
    public boolean findIfUserLikedFilm(Long filmId, Long userId) {
        String query = "SELECT user_id FROM likes WHERE film_id = ? AND user_id = ?";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, filmId, userId);
            if (rowSet.next()) {
                if (userId == rowSet.getLong("user_id")) {
                    return true;
                }
            }
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
        return false;
    }

    @Override
    public List<Film> findFilmsBy(String query, By by) {
        query = "%" + query.toLowerCase() + "%";
        StringBuilder where = new StringBuilder("WHERE ");
        if (by.isDirector()) {
            where.append("lower(d.name) LIKE :query OR ");
        }
        if (by.isTitle()) {
            where.append("lower(title) LIKE :query OR ");
        }
        where.delete(where.length() - 3, where.length());
        String sql = "SELECT f.*, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name, " +
                "d.id AS director_id, d.name AS director_name " +
                "FROM films AS f INNER JOIN mpa AS m ON m.id = f.mpa_id " +
                "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
                "LEFT JOIN genre AS g ON g.id = fg.genre_id " +
                "LEFT JOIN film_directors AS fd ON fd.film_id = f.id " +
                "LEFT JOIN directors d ON fd.director_id = d.id " + where + "ORDER BY f.rate DESC";
        NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
        SqlParameterSource namedParameter = new MapSqlParameterSource().addValue("query", query);
        SqlRowSet rowSet = jdbc.queryForRowSet(sql, namedParameter);
        return FilmMapper.makeFilmList(rowSet);
    }
}
