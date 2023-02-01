package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.dto.By;
import ru.yandex.practicum.filmorate.exception.FilmorateValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info(String.format("FilmController: create film request. Data: %s", film));
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info(String.format("FilmController: update film request. Data: %s)", film));
        return filmService.update(film);
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("FilmController: get all films request.");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film findFilmBy(@PathVariable("id") Long id) {
        log.info(String.format("FilmController: get film with id: %d", id));
        return filmService.getFilmBy(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public int setFilmLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info(String.format("FilmController: Adding like for Film with %d id from User with %d id", id, userId));
        return filmService.setFilmLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public int removeFilmLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info(String.format("FilmController: Removing like for Film with %d id from User with %d id", id, userId));
        return filmService.removeFilmLike(id, userId);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmBy(@PathVariable("filmId") Long filmId) {
        filmService.deleteFilmBy(filmId);
        log.info(String.format("FilmController: Remove film with id: %d.", filmId));
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(name = "count", defaultValue = "10") int count,
                                      @RequestParam(name = "genreId", required = false) Integer genreId,
                                      @RequestParam(name = "year", required = false) Integer year) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/search")
    public List<Film> findFilmBy(@RequestParam(name = "query") String query, By by) {
        log.info("FilmController: search query: {} search by: {}", query, by);
        return filmService.findFilmsBy(query, by);
    }

    // GIR: class Directors
    //GET /films/director/{directorId}?sortBy=[year,likes]  - добавить в FilmController
    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsDirectorBySort(@PathVariable Integer directorId,
                                                   @RequestParam(value = "sortBy") String sort) {
        if (sort.equals("likes")) {
            return filmService.getDirectorFilmSortedByLike(directorId);
        }
        if (sort.equals("year")) {
            return filmService.getDirectorFilmSortedByYear(directorId);
        } else {
            throw new FilmorateValidationException("Неверный запрос");
        }
    }
}
