package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmLikeDao;
import ru.yandex.practicum.filmorate.dao.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmLikeDb implements FilmLikeDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean addFilmLike(Long filmId, Long userId) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("likes");
        try {
            return simpleJdbcInsert.execute(likesToParameters(userId, filmId)) == 1;
        } catch (DuplicateKeyException e) {
            log.debug(String.format("FilmLikes: trying to add duplicate like from User id: %d to Film Id: %d", userId, filmId));
            return false;
        } catch (DataIntegrityViolationException e) {
            throw new FilmorateNotFoundException(
                    String.format("Фильм с id: %d или пользователь с id: %d не найдены.", filmId, userId));
        }
    }

    @Override
    public boolean removeFilmLike(Long filmId, Long userId) {
        String query = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        return jdbcTemplate.update(query, userId, filmId) == 1;
    }

    private Map<String, Object> likesToParameters(Long userId, Long filmId) {

        Map<String, Object> parameters = new HashMap<>();

        parameters.put("user_id", userId);
        parameters.put("film_id", filmId);

        return parameters;
    }

    @Override
    public List<Film> getSameLikesByUser(Long userId, int count) {
        String query = "SELECT f.*, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name, fd.DIRECTOR_ID, d.NAME AS DIRECTOR_NAME " +
                "FROM films AS f " +
                "INNER JOIN mpa AS m ON m.id = f.mpa_id " +
                "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
                "LEFT JOIN genre AS g ON g.id = fg.genre_id " +
                "LEFT JOIN FILM_DIRECTORS fd on f.ID = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS d on fd.DIRECTOR_ID = d.ID " +
                "WHERE f.id IN ( " +
                "  (SELECT film_id FROM likes WHERE user_id IN ( " +
                "    SELECT flm.user_id AS user_id from likes AS flm " +
                "    INNER JOIN likes AS lk ON lk.film_id = flm.film_id AND lk.user_id = ?" +
                "    GROUP BY flm.user_id HAVING flm.user_id <> ?" +
                "    ORDER BY COUNT(flm.user_id) DESC )  " +
                "  EXCEPT " +
                "  SELECT film_id FROM likes WHERE user_id = ?) LIMIT ? ) ";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, userId, userId, userId, count);
        return FilmMapper.makeFilmList(rowSet);
    }
}
