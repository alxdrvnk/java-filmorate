package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        String query = "SELECT * FROM genre ORDER BY id";
        return jdbcTemplate.query(query, new GenreMapper());
    }

    @Override
    public Optional<Genre> getBy(Long id) {
        String query = "SELECT * FROM genre WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, new GenreMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
