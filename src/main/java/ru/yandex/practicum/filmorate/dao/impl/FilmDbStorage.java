package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.dao.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.spi.LocaleServiceProvider;

@Slf4j
@RequiredArgsConstructor
@Component("filmDBStorage")
@Primary
public class FilmDbStorage implements FilmDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        Long filmId = simpleJdbcInsert.executeAndReturnKey(filmToParameters(film)).longValue();
        return film.withId(filmId);
    }

    @Override
    public Film update(Film film) {
        String query = "UPDATE films SET title = ?, description = ?, release_date = ?, duration = ?, rate = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());
        return film;
    }

    @Override
    public int deleteBy(Long id) {
        String query = "DELETE FROM films WHERE id = ?";
        return jdbcTemplate.update(query, id);
    }

    @Override
    public List<Film> getAll() {
        String query =
                "SELECT f.*, m.name AS mpa_name FROM films AS f " +
                        "JOIN mpa AS m On f.mpa_id = m.id " +
                        "ORDER BY f.id";
        return jdbcTemplate.query(query, this::makeFilm);
    }

    @Override
    public Optional<Film> getBy(Long id) {
        String query =
                "SELECT f.*, m.name AS mpa_name FROM films AS f " +
                        "INNER JOIN mpa AS m ON f.mpa_id = m.id " +
                        "WHERE f.id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, new FilmMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String query = "SELECT *, mpa.name AS mpa_name FROM films AS flm " +
                       "INNER JOIN mpa ON mpa.id = flm.mpa_id " +
                       "ORDER BY flm.rate DESC " +
                       "LIMIT ?";

        return jdbcTemplate.query(query, new FilmMapper(), count);
    }

    private Film makeFilm(ResultSet rs,int rowNum) throws SQLException {
        Map<Long, Film> filmById = new HashMap<>();

        while (rs.next()) {
            Long id = rs.getLong("id");
            String title = rs.getString("title");
            String description = rs.getString("description");
            LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
            int duration = rs.getInt("duration");
            int rate = rs.getInt("rate");
            Mpa mpa = new MpaMapper().mapRow(rs, rowNum);
            Film film = filmById.get(id);

            if (film == null) {
                film = Film.builder()
                        .id(id)
                        .name(title)
                        .description(description)
                        .releaseDate(releaseDate)
                        .duration(duration)
                        .rate(rate)
                        .mpa(mpa).build();
                filmById.put(film.getId(), film);
            }
        }
    }

    private Map<String, Object> filmToParameters(Film film) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", film.getId());
        parameters.put("title", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", film.getReleaseDate());
        parameters.put("duration", film.getDuration());
        parameters.put("rate", film.getRate());
        parameters.put("mpa_id", film.getMpa().getId());
        return parameters;
    }

}