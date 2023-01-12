package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.dao.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

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
                "SELECT f.*, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name FROM films AS f " +
                        "JOIN mpa AS m ON f.mpa_id = m.id " +
                        "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
                        "LEFT JOIN genre AS g ON g.id = fg.genre_id " +
                        "ORDER BY f.id";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query);
            return makeFilm(rowSet);
        } catch (SQLException e){
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Film> getBy(Long id) {
        String query =
                "SELECT f.*, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name FROM films AS f " +
                        "INNER JOIN mpa AS m ON f.mpa_id = m.id " +
                        "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
                        "LEFT JOIN genre AS g ON g.id = fg.genre_id " +
                        "WHERE f.id = ?";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, id);
            return makeFilm(rowSet).stream().findAny();
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String query = "SELECT f.*, m.name AS mpa_name, g.id AS genre_id, g.name AS genre_name FROM films AS f " +
                       "INNER JOIN mpa AS m ON m.id = f.mpa_id " +
                       "LEFT JOIN film_genres AS fg ON fg.film_id = f.id " +
                       "LEFT JOIN genre AS g ON g.id = fg.genre_id " +
                       "ORDER BY f.rate DESC " +
                       "LIMIT ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, count);
        try {
            return makeFilm(rowSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Film> makeFilm(SqlRowSet rs) throws SQLException {

        Map<Long, Film> filmById = new HashMap<>();

        while (rs.next()) {
            Long id = rs.getLong("id");
            String title = rs.getString("title");
            String description = rs.getString("description");
            LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
            int duration = rs.getInt("duration");
            int rate = rs.getInt("rate");
            long mpaID = rs.getLong("mpa_id");
            String name = rs.getString("MPA_NAME");
            Mpa mpa = Mpa.builder()
                    .id(rs.getLong("mpa_id"))
                    .name(rs.getString("mpa_name"))
                    .build();

            Genre genre = Genre.builder()
                    .id(rs.getLong("genre_id"))
                    .name(rs.getString("genre_name"))
                    .build();

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
            if (genre.getId() != 0) {
                List<Genre> genres = new ArrayList<>(film.getGenres());
                genres.add(genre);
                filmById.put(film.getId(), film.withGenres(genres));
            }
        }
        return new ArrayList<>(filmById.values());
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