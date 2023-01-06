package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDb implements MpaDao {

    private final JdbcTemplate jdbcTemplate;
    @Override
    public List<Mpa> getAll() {
        String query = "SELECT * FROM mpa";
        return jdbcTemplate.query(query, new MpaMapper());
    }

    @Override
    public Optional<Mpa> getBy(Long id) {
        String query = "SELECT * FROM mpa WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, new MpaMapper() ,id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
