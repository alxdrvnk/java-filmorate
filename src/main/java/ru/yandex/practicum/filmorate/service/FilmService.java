package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmorateValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private static final LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);

    private final FilmDao storage;
    private final FilmGenreDao filmGenresStorage;
    private final MpaDao mpaStorage;


    public Film create(Film film) {
        validateReleaseDate(film);

        film = storage.create(film);

        filmGenresStorage.addFilmGenres(film.getId(), film.getGenres());
        List<Genre> genres = filmGenresStorage.getFilmGenres(film.getId());

        Mpa mpa = mpaStorage.getBy(film.getMpa().getId()).orElseThrow(
                () -> new FilmorateNotFoundException("Mpa рейтинг не найден."));

        return film.withGenres(genres).withMpa(mpa);
    }

    public List<Film> getAllFilms() {
        return storage.getAll();
    }

    public Film getFilmBy(Long id) {
        return storage.getBy(id).orElseThrow(() -> new FilmorateNotFoundException("Фильм с id: не найден."));
    }

    public Film update(Film film) {
        validateReleaseDate(film);
        getFilmBy(film.getId());
        return storage.update(film);
    }

    // Лучше возвращать объект фильма при добавлении/удалении лайка?
    public void setFilmLike(Long filmId, Long userId) {
//        Film film = getFilmBy(filmId);
//        userService.addLikedFilm(userId, filmId);
//        storage.addLike(film);
    }

    public void removeFilmLike(Long filmId, Long userId) {
//        Film film = getFilmBy(filmId);
//        userService.removeLikedFilm(userId, filmId);
//        storage.removeLike(film);
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

    public void deleteFilmBy(Long id) {
        getFilmBy(id);
        storage.deleteBy(id);
    }
}
