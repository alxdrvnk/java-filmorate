package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dao.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("id");
        Long reviewId = simpleJdbcInsert.executeAndReturnKey(reviewToParameters(review)).longValue();
        return review.withReviewId(reviewId);
    }

    @Override
    public Optional<Review> getBy(Long id) {
        String query = "SELECT * FROM reviews WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, new ReviewMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Review> getAll() {
        String query = "SELECT * FROM reviews ORDER BY useful DESC";
        return jdbcTemplate.query(query, new ReviewMapper());
    }

    @Override
    public Review update(Review review) {
        String query = "UPDATE reviews SET content = ?, is_positive = ?, useful = ? WHERE id = ?";
        jdbcTemplate.update(query,
                review.getContent(),
                review.getIsPositive(),
                review.getUseful(),
                review.getReviewId()
        );
        return getBy(review.getReviewId()).orElseThrow(() -> new FilmorateNotFoundException("Отзыв не найден"));
    }

    @Override
    public int deleteBy(Long id) {
        String query = "DELETE FROM reviews WHERE id = ?";
        return jdbcTemplate.update(query, id);
    }

    @Override
    public List<Review> getByFilm(Long filmId, Integer count) {
        SqlRowSet rowSet;
        if (filmId != null) {
            rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?", filmId, count);
        } else {
            rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM reviews ORDER BY useful DESC LIMIT ?", count);
        }
        return ReviewMapper.makeReviewList(rowSet);
    }

    private Map<String, Object> reviewToParameters(Review review) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", review.getReviewId());
        parameters.put("user_id", review.getUserId());
        parameters.put("film_id", review.getFilmId());
        parameters.put("content", review.getContent());
        parameters.put("is_positive", review.getIsPositive());
        parameters.put("useful", review.getUseful());
        return parameters;
    }
}
