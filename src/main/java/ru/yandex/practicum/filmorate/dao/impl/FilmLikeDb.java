package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmLikeDao;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;

import java.util.ArrayList;
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
    public Map<Long, List<Long>> getSameLikesByUser(Long userId) {
        String query = "SELECT * FROM likes WHERE user_id IN (SELECT DISTINCT user_id FROM LIKES WHERE film_id IN " +
                "(SELECT film_id FROM likes WHERE user_id = ?))";

        Map<Long, List<Long>> likes = new HashMap<>();
        jdbcTemplate.query(query, rs -> {
            long id = rs.getLong("user_id");
            long filmId = rs.getLong("film_id");
            likes.putIfAbsent(id, new ArrayList<>());
            likes.get(id).add(filmId);
        }, userId);
        return likes;
    }
}
