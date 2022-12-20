package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmorateValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private static final LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);
    private final FilmStorage storage;
    private final UserService userService;

    public Film create(Film film) {
        validateReleaseDate(film);
        return storage.create(film);
    }

    public List<Film> getAllFilms() {
        return storage.getAllFilms();
    }

    public Film getFilmBy(Long id) {
        return storage.get(id);
    }

    public Film update(Film film) {
        validateReleaseDate(film);
        return storage.update(film);
    }

    public Film setFilmLike(Long filmId, Long userId) {
        Film film = getFilmBy(filmId);
        userService.addLikedFilm(userId, filmId);
        return storage.addLike(film);
    }

    public Film removeFilmLike(Long filmId, Long userId) {
        Film film = getFilmBy(filmId);
        userService.removeLikedFilm(userId, filmId);
        return storage.removeLike(film);
    }

    public List<Film> getPopularFilms(int count) {
        return storage.getPopular(count);
    }


    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(cinemaBirthday)) {
            throw new FilmorateValidationException(
                    String.format("Дата релиза не может быть раньше чем %s", cinemaBirthday));
        }
    }
}
