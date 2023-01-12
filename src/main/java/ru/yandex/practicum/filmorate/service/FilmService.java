package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.FilmLikeDao;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmorateValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private static final LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);
    private final FilmDao storage;
    private final MpaService mpaService;
    private final FilmLikeDao filmLikeDao;
    private final FilmGenreDao filmGenresDao;
    private final UserService userService;


    public Film create(Film film) {
        validateReleaseDate(film);

        Film newFilm = storage.create(film);

        filmGenresDao.updateFilmGenres(newFilm.getId(), film.getGenres());
        List<Genre> genres = filmGenresDao.getFilmGenres(newFilm.getId());

        Mpa mpa = mpaService.getById(film.getMpa().getId());
        return newFilm.withGenres(genres).withMpa(mpa);
    }

    public List<Film> getAllFilms() {
        List<Film> films = new ArrayList<>();
        storage.getAll().forEach(
                film -> films.add(film.withGenres(filmGenresDao.getFilmGenres(film.getId()))));
        return films;
    }

    public Film getFilmBy(Long id) {
        Film film = storage.getBy(id).orElseThrow(() -> new FilmorateNotFoundException("Фильм с id: не найден."));
        return film.withGenres(filmGenresDao.getFilmGenres(id));
    }

    public Film update(Film film) {
        validateReleaseDate(film);
        getFilmBy(film.getId());

        storage.update(film);
        filmGenresDao.updateFilmGenres(film.getId(), film.getGenres());

        List<Genre> genres = filmGenresDao.getFilmGenres(film.getId());
        Mpa mpa = mpaService.getById(film.getMpa().getId());

        return film.withMpa(mpa).withGenres(genres);
    }

    public void setFilmLike(Long filmId, Long userId) {
        filmLikeDao.addFilmLike(filmId, userId);
        Film film = getFilmBy(filmId);
        update(film.withRate(film.getRate() + 1));
    }

    public void removeFilmLike(Long filmId, Long userId) {
        Film film = getFilmBy(filmId);
        userService.getUserBy(userId);
        filmLikeDao.removeFilmLike(filmId, userId);
        update(film.withRate(film.getRate()-1));
    }

    public Long getFilmsLikesCount(Long filmId) {
        return getFilmBy(filmId).getRate();
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> films = new ArrayList<>();
        storage.getPopularFilms(count).forEach(
                film -> films.add(film.withGenres(filmGenresDao.getFilmGenres(film.getId()))));
        return films;
    }

    public void deleteFilmBy(Long id) {
        if (storage.deleteBy(id) == 0) {
            throw new FilmorateNotFoundException(
                    String.format("Фмльм с id: %d не найден", id));
        }
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(cinemaBirthday)) {
            throw new FilmorateValidationException(
                    String.format("Дата релиза не может быть раньше чем %s", cinemaBirthday));
        }
    }
}
