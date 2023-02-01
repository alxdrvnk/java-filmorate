package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.dto.By;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.FilmLikeDao;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmorateValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.FilmorateEventOperation;
import ru.yandex.practicum.filmorate.utils.FilmorateEventType;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private static final LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);
    private final FilmDao storage;
    private final FilmLikeDao filmLikeDao;
    private final FilmGenreDao filmGenresDao;
    private final UserService userService;
    private final EventsService eventsService;

    public Film create(Film film) {
        validateReleaseDate(film);
        Film newFilm = storage.create(film.withRate(0));

        storage.addDirectorForFilm(newFilm);
        filmGenresDao.updateFilmGenres(newFilm.getId(), film.getGenres());

        return getFilmBy(newFilm.getId());
    }

    public List<Film> getAllFilms() {
        return storage.getAll();
    }

    public Film getFilmBy(Long id) {
        return storage.getBy(id).orElseThrow(() -> new FilmorateNotFoundException(
                String.format("Фильм с id: %d не найден.", id)));
    }

    public Film update(Film film) {
        validateReleaseDate(film);
        storage.update(film.withRate(getFilmBy(film.getId()).getRate()));
        storage.deleteDirectorForFilm(film.getId());
        storage.addDirectorForFilm(film);
        filmGenresDao.updateFilmGenres(film.getId(), film.getGenres());
        return getFilmBy(film.getId());
    }

    public int setFilmLike(Long filmId, Long userId) {
        Film film = getFilmBy(filmId);
        userService.getUserBy(userId);
        if (storage.findIfUserLikedFilm(filmId, userId)) {
            return film.getRate();
        }
        filmLikeDao.addFilmLike(filmId, userId);

        int likes = film.getRate() + 1;
        storage.update(film.withRate(likes));

        eventsService.create(userId, filmId, FilmorateEventType.LIKE, FilmorateEventOperation.ADD);
        return likes;
    }

    public int removeFilmLike(Long filmId, Long userId) {
        Film film = getFilmBy(filmId);
        userService.getUserBy(userId);
        filmLikeDao.removeFilmLike(filmId, userId);
        int likes = film.getRate() - 1;
        storage.update(film.withRate(likes));

        eventsService.create(userId, filmId, FilmorateEventType.LIKE, FilmorateEventOperation.REMOVE);

        return likes;
    }

    public int getFilmsLikesCount(Long filmId) {
        return getFilmBy(filmId).getRate();
    }

    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        return storage.getPopularFilms(count, genreId, year);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return storage.getCommonFilms(userId, friendId);
    }

    public void deleteFilmBy(Long id) {
        storage.deleteDirectorForFilm(id);
        if (storage.deleteBy(id) == 0) {
            throw new FilmorateNotFoundException(
                    String.format("Фильм с id: %d не найден.", id));
        }
    }

    public List<Film> getDirectorFilmSortedByLike(int directorId) {
        List<Film> films = storage.getDirectorFilmSortedByLike(directorId);
        if (films.isEmpty()) {
            throw new FilmorateNotFoundException("У режиссера с id = " + directorId + " нет фильмов");
        }
        return films;
    }

    public List<Film> getDirectorFilmSortedByYear(int directorId) {
        List<Film> films = storage.getDirectorFilmSortedByYear(directorId);
        if (films.isEmpty()) {
            throw new FilmorateNotFoundException("У режиссера с id = " + directorId + " нет фильмов");
        }
        return films;
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(cinemaBirthday)) {
            throw new FilmorateValidationException(
                    String.format("Дата релиза не может быть раньше чем %s", cinemaBirthday));
        }
    }

    public List<Film> findFilmsBy(String query, By by) {
        return storage.findFilmsBy(query, by);
    }
}
