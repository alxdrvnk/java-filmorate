package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("film_genres");
            try {
                genreIds.forEach(i -> simpleJdbcInsert.execute(filmGenresToParameters(filmId, i.getId())));
            } catch (DuplicateKeyException e) {
                log.debug(
                        String.format("FilmGenres: trying to add duplicate genre to Film id: %d",
                                filmId));
            }
        }
    }

    private Map<String, Object> filmGenresToParameters(Long filmId, Long genreIds) {

        Map<String, Object> parameters = new HashMap<>();

        parameters.put("film_id", filmId);
        parameters.put("genre_id", genreIds);

        return parameters;
    }
}
