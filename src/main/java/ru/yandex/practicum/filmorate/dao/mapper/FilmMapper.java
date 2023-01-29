package ru.yandex.practicum.filmorate.dao.mapper;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.*;

public class FilmMapper {

    public static List<Film> makeFilmList(SqlRowSet rs) {

        Map<Long, Film> filmById = new LinkedHashMap<>();

        while (rs.next()) {
            Long id = rs.getLong("ID");
            String title = rs.getString("TITLE");
            String description = rs.getString("DESCRIPTION");
            LocalDate releaseDate = rs.getDate("RELEASE_DATE").toLocalDate();
            int duration = rs.getInt("DURATION");
            int rate = rs.getInt("RATE");
            Mpa mpa = makeMpa(rs);
            Genre genre = makeGenre(rs);
            Director director = makeDirector(rs);

            Film film = filmById.get(id);

            if (film == null) {
                film = Film.builder()
                        .id(id)
                        .name(title)
                        .description(description)
                        .releaseDate(releaseDate)
                        .duration(duration)
                        .rate(rate)
                        .mpa(mpa)
                        .build();
                filmById.put(film.getId(), film);
            }

            if (genre.getId() != 0) {
                List<Genre> genres = new ArrayList<>(film.getGenres());
                genres.add(genre);
                filmById.put(film.getId(), film.withGenres(genres));
            }

            if (director.getId() != 0) {
                List<Director> directors = new ArrayList<>(film.getDirectors());
                directors.add(director);
                filmById.put(film.getId(), film.withDirectors(directors));
            }
        }
        return new ArrayList<>(filmById.values());
    }

    private static Mpa makeMpa(SqlRowSet rs) {
        return Mpa.builder()
                .id(rs.getLong("MPA_ID"))
                .name(rs.getString("MPA_NAME"))
                .build();
    }

    private static Genre makeGenre(SqlRowSet rs) {
        return Genre.builder()
                .id(rs.getLong("GENRE_ID"))
                .name(rs.getString("GENRE_NAME"))
                .build();
    }

    private static Director makeDirector(SqlRowSet rs) {
        return  Director.builder()
                .id(rs.getInt("DIRECTOR_ID"))
                .name(rs.getString("DIRECTOR_NAME"))
                .build();
    }
}

