package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmSotrage implements FilmStorage{

    private static final LocalDate cinemaBirthday = LocalDate.of(1894, 12, 28);

    private final HashMap<Long, Film> films = new HashMap<>();

    private static Long id = 1L;

    private static Long getNextId() {
        return id++;
    }

    @Override
    public Film create(Film film) {
        Long id = getNextId();
        Film newFilm = film.withId(id);
        films.put(id, newFilm);
        return newFilm;
    }

    @Override
    public Film update(Film film) {
        return null;
    }

    @Override
    public List<Film> getAllFilms() {
        return null;
    }

    @Override
    public Film get(Long id) {
        return null;
    }

    @Override
    public List<Film> getPopular(int count) {
        return null;
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        return null;
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        return null;
    }
}
