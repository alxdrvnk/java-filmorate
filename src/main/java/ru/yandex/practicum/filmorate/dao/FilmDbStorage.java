package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component("filmDBStorage")
@Primary
public class FilmDbStorage implements FilmDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("id");
        return simpleJdbcInsert.executeAndReturnKey(filmToParameters(film)).longValue();
    }

    @Override
    public void update(Film film) {
        String sql = "UPDATE film SET title = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getTitle(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa(),
                film.getId());
    }

    @Override
    public void deleteBy(Long id) {
        String sql = "DELETE FROM film WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT * FROM films AS f";
        return jdbcTemplate.query(sql, new FilmMapper());
    }

    @Override
    public Optional<Film> getBy(Long id) {
        String sql = "SELECT id, title, description, release_date, duration, mpa_id FROM film WHERE id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new FilmMapper()));
    }

    @Override
    public List<Film> getPopular(int count) {
        return null;
    }

    @Override
    public Long addLike(Film film) {
        return null;
    }

    @Override
    public Long removeLike(Film film) {
        return null;
    }

    private Map<String, Object> filmToParameters(Film film) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", film.getId());
        parameters.put("title", film.getTitle());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", film.getReleaseDate());
        parameters.put("duration", film.getDuration());
        parameters.put("map_id", film.getMpa());
        return parameters;
    }

    private static final class FilmMapper implements RowMapper<Film> {

        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Film.builder()
                    .id(rs.getLong("id"))
                    .title(rs.getString("title"))
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .duration(rs.getInt("duration"))
                    .mpa(rs.getInt("mpa_id")).build();
        }
    }
}