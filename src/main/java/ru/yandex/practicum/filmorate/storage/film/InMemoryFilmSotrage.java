package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmorateValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmSotrage implements FilmStorage {

    private static final LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);

    private final HashMap<Long, Film> films = new HashMap<>();

    private static Long id = 1L;

    private static Long getNextId() {
        return id++;
    }

    @Override
    public Film create(Film film) {
        validateReleaseDate(film);
        Long id = getNextId();
        Film newFilm = film.withId(id);
        films.put(id, newFilm);
        return newFilm;
    }

    @Override
    public Film update(Film film) {
        validateReleaseDate(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new FilmorateNotFoundException(String.format("Фильм с id: %d не найден.", film.getId()));
        }
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film get(Long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new FilmorateNotFoundException(String.format("Фильм с id: %d не найден.", id));
        }
    }

    @Override
    public List<Film> getPopular(int count) {
        return null;
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        film.getLikes().add(userId);
        return film;
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        film.getLikes().remove(userId);
        return film;
    }
    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(cinemaBirthday)) {
            throw new FilmorateValidationException(
                    String.format("Дата релиза не может быть раньше чем %s", cinemaBirthday));
        }
    }
}
