package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.aspects.HandleFilmorateEvent;
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

    public Film create(Film film) {
        validateReleaseDate(film);

        Film newFilm = storage.create(film);

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
        getFilmBy(film.getId());

        storage.update(film);
        filmGenresDao.updateFilmGenres(film.getId(), film.getGenres());

        return getFilmBy(film.getId());
    }

    @HandleFilmorateEvent(eventType = FilmorateEventType.LIKE, eventOperation = FilmorateEventOperation.ADD)
    public int setFilmLike(Long userId, Long filmId) {
        Film film = getFilmBy(filmId);
        filmLikeDao.addFilmLike(filmId, userId);

        int likes = film.getRate() + 1;
        update(film.withRate(likes));
        return likes;
    }

    @HandleFilmorateEvent(eventType = FilmorateEventType.LIKE, eventOperation = FilmorateEventOperation.REMOVE)
    public int removeFilmLike(Long userId, Long filmId) {

        Film film = getFilmBy(filmId);
        userService.getUserBy(userId);
        filmLikeDao.removeFilmLike(filmId, userId);

        int likes = film.getRate()-1;
        update(film.withRate(likes));
        return likes;
    }

    public int getFilmsLikesCount(Long filmId) {
        return getFilmBy(filmId).getRate();
    }

    public List<Film> getPopularFilms(int count) {
        return storage.getPopularFilms(count);
    }

    public void deleteFilmBy(Long id) {
        if (storage.deleteBy(id) == 0) {
            throw new FilmorateNotFoundException(
                    String.format("Фильм с id: %d не найден.", id));
        }
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(cinemaBirthday)) {
            throw new FilmorateValidationException(
                    String.format("Дата релиза не может быть раньше чем %s", cinemaBirthday));
        }
    }
}
