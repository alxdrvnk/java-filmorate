package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDirectorDao;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmDirectorsDbStorage implements FilmDirectorDao {

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
