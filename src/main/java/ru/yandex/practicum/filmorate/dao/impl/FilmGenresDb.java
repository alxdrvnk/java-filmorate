package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmGenresDb implements FilmGenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getFilmGenres(Long filmId) {
        String query = "SELECT * FROM genre " +
                       "WHERE id IN (SELECT genre_id FROM film_genres WHERE film_id = ?)";
        return jdbcTemplate.query(query, new GenreMapper(), filmId);
    }

    @Override
    public void updateFilmGenres(Long filmId, List<Genre> genreIds) {

        String queryDel = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(queryDel, filmId);

        if (!genreIds.isEmpty()) {
            String query = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            genreIds.forEach(i -> jdbcTemplate.update(query, filmId, i.getId()));
        }
    }
}
