package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmLikeDao;
import ru.yandex.practicum.filmorate.dao.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmLikeDb implements FilmLikeDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFilmLike(Long filmId, Long userId) {
        String query = "INSERT INTO likes (user_id,film_id) VALUES (?,?)";
        jdbcTemplate.update(query, userId, filmId);
    }

    @Override
    public void removeFilmLike(Long filmId, Long userId) {
        String query = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(query, userId, filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String query = "SELECT flm.*, mpa.name AS mpa_name, COALESCE(fl.film_likes,0) AS likes " +
                       "FROM films AS flm " +
                       "INNER JOIN mpa ON mpa.id = flm.mpa_id " +
                       "LEFT JOIN (SELECT lk.film_id, COUNT(user_id) AS film_likes " +
                                  "FROM likes AS lk " +
                                  "GROUP BY lk.film_id) AS fl ON fl.film_id = flm.id " +
                       "ORDER BY likes DESC " +
                       "LIMIT ?";
        return jdbcTemplate.query(query, new FilmMapper(), count);
    }
}
