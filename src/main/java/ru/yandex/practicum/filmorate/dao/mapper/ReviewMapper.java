package ru.yandex.practicum.filmorate.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .useful(rs.getInt("useful"))
                .build();
    }

    public static List<Review> makeReviewList(SqlRowSet rs) {
        List<Review> reviews = new ArrayList<>();
        while (rs.next()) {
            Review review = Review.builder()
                    .reviewId(rs.getLong("id"))
                    .userId(rs.getLong("user_id"))
                    .filmId(rs.getLong("film_id"))
                    .content(rs.getString("content"))
                    .isPositive(rs.getBoolean("is_positive"))
                    .useful(rs.getInt("useful"))
                    .build();
            reviews.add(review);
        }
        return reviews;
    }
}
