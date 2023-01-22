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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmLikeDb implements FilmLikeDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFilmLike(Long filmId, Long userId) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("likes");
        try {
            simpleJdbcInsert.execute(likesToParameters(userId, filmId));
        } catch (DuplicateKeyException e) {
            log.debug(String.format("FilmLikes: trying to add duplicate like from User id: %d to Film Id: %d", userId, filmId));
        } catch (DataIntegrityViolationException e) {
            throw new FilmorateNotFoundException(
                    String.format("Фильм с id: %d или пользователь с id: %d не найдены.", filmId, userId));
        }
    }

    @Override
    public void removeFilmLike(Long filmId, Long userId) {
        String query = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(query, userId, filmId);
    }

    private Map<String, Object> likesToParameters(Long userId, Long filmId) {

        Map<String, Object> parameters = new HashMap<>();

        parameters.put("user_id", userId);
        parameters.put("film_id", filmId);

        return parameters;
    }
}
