package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDirectorDao;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmDirectorsDb implements FilmDirectorDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void createFilmDirectors(Film film) {
        jdbcTemplate.batchUpdate("INSERT INTO film_directors (director_id, film_id) " +
                        "VALUES (?, ?)",
                film.getDirectors(),
                100,
                (PreparedStatement ps, Director director) -> {
                    ps.setInt(1, director.getId());
                    ps.setLong(2, film.getId());
                });
    }

    @Override
    public void updateFilmDirectors(Film film) {
        String queryDel = "DELETE FROM film_directors WHERE film_id = ?";
        jdbcTemplate.update(queryDel, film.getId());
        createFilmDirectors(film);
    }
}
