package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
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
    public void addFilmGenres(Long filmId, List<Genre> genreIds) {
        String query = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        if (!genreIds.isEmpty()) {
            genreIds.forEach(i -> jdbcTemplate.update(query, filmId, i.getId()));
        }
    }

    @Override
    public void removeFilmGenre(Long filmId, Long genreId) {
        String query = "DELETE FROM film_genres WHERE film_id = ? AND genre_id = ?";
        jdbcTemplate.update(query, filmId, genreId);
    }
}
