package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.ReviewLikeDao;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewLikeDb implements ReviewLikeDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean addLike(Long reviewId, Long userId) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("review_likes");
        try {
            return simpleJdbcInsert.execute(likesToParameters(reviewId, userId, true)) == 1;
        } catch (DuplicateKeyException e) {
            log.debug(String.format("ReviewLikes: trying to add duplicate like from User id: %d to Review Id: %d", userId, reviewId));
        } catch (DataIntegrityViolationException e) {
            throw new FilmorateNotFoundException(
                    String.format("ReviewLikes: Review with id: %d or user with id: %d not found.", reviewId, userId));
        }
        return false;
    }

    @Override
    public boolean addDislike(Long reviewId, Long userId) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("review_likes");
        try {
            return simpleJdbcInsert.execute(likesToParameters(reviewId, userId, false)) == 1;
        } catch (DuplicateKeyException e) {
            log.debug(String.format("ReviewLikes: trying to add duplicate dislike from User id: %d to Review Id: %d", userId, reviewId));
        } catch (DataIntegrityViolationException e) {
            throw new FilmorateNotFoundException(
                    String.format("ReviewLikes: Review with id: %d or user with id: %d not found.", reviewId, userId));
        }
        return false;
    }

    @Override
    public boolean removeLike(Long reviewId, Long userId) {
        String query = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = true";
        return jdbcTemplate.update(query, reviewId, userId) == 1;
    }

    @Override
    public boolean removeDislike(Long reviewId, Long userId) {
        String query = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = false";
        return jdbcTemplate.update(query, reviewId, userId) == 1;
    }

    private Map<String, Object> likesToParameters(Long reviewId, Long userId, boolean value) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user_id", userId);
        parameters.put("review_id", reviewId);
        parameters.put("is_like", value);
        return parameters;
    }
}
