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
        /*String query =
                "SELECT f.*, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name FROM films AS f " +
                        "JOIN mpa AS m ON f.mpa_id = m.id " +
                        "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
                        "LEFT JOIN genre AS g ON g.id = fg.genre_id " +
                        "ORDER BY f.id";*/
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

        /*String query =
                "SELECT f.*, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name FROM films AS f " +
                        "INNER JOIN mpa AS m ON f.mpa_id = m.id " +
                        "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
                        "LEFT JOIN genre AS g ON g.id = fg.genre_id " +
                        "WHERE f.id = ?";*/
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

/*        String query = "SELECT f.*, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name FROM films AS f " +
                "INNER JOIN mpa AS m ON m.id = f.mpa_id " +
                "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
                "LEFT JOIN genre AS g ON g.id = fg.genre_id " +
                "ORDER BY f.rate DESC " +
                "LIMIT ?";*/
        String query = "SELECT f.*, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name, fd.DIRECTOR_ID, d.NAME AS DIRECTOR_NAME " +
                "FROM films AS f " +
                "INNER JOIN mpa AS m ON m.id = f.mpa_id  " +
                "LEFT JOIN film_genres AS fg ON fg.film_id = f.id  " +
                "LEFT JOIN genre AS g ON g.id = fg.genre_id  " +
                "LEFT JOIN FILM_DIRECTORS fd on f.ID = fd.FILM_ID  " +
                "LEFT JOIN DIRECTORS d on fd.DIRECTOR_ID = d.ID  " +
                "ORDER BY f.rate  " +
                "DESC LIMIT ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, count);

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
    public void addDirectorForFilm(Film film){
        if (film.getDirectors().size() != 0){
            for (Director director: film.getDirectors()) {
            jdbcTemplate.update("INSERT INTO FILM_DIRECTORS (DIRECTOR_ID, FILM_ID) VALUES ( ?, ? )", director.getId(), film.getId());
            }
        }
    }
    @Override
    public void deleteDirectorForFilm(Film film){
        jdbcTemplate.update("DELETE FROM FILM_DIRECTORS WHERE FILM_ID = ?", film.getId());
    }

}