package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmorateValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private static final LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);

    @Qualifier("filmDBStorage")
    private final FilmDao storage;
    private final UserService userService;

    public Long create(Film film) {
        validateReleaseDate(film);
        return storage.create(film);
    }

    public List<Film> getAllFilms() {
        return storage.getAll();
    }

    public Film getFilmBy(Long id) {
        return storage.getBy(id).orElseThrow(() -> new FilmorateNotFoundException("Фильм с id: не найден."));
    }

    public void update(Film film) {
        validateReleaseDate(film);
        storage.update(film);
    }

    // Лучше возвращать объект фильма при добавлении/удалении лайка?
    public void setFilmLike(Long filmId, Long userId) {
        Film film = getFilmBy(filmId);
        userService.addLikedFilm(userId, filmId);
        storage.addLike(film);
    }

    public void removeFilmLike(Long filmId, Long userId) {
        Film film = getFilmBy(filmId);
        userService.removeLikedFilm(userId, filmId);
        storage.removeLike(film);
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
