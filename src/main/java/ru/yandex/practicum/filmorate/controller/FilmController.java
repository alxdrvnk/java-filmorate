package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
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
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film findFilmBy(@PathVariable("id") Long id) {
        return filmService.getFilmBy(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setFilmLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        filmService.setFilmLike(id, userId);
        log.info(String.format("FilmController: Add like for Film with %d id from User with %d id", id, userId));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeFilmLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        filmService.removeFilmLike(id, userId);
        log.info(String.format("FilmController: Remove like for Film with %d id from User with %d id", id, userId));
    }

    @DeleteMapping("/{id}")
    public void deleteFilmBy(@PathVariable("id") Long id) {
        filmService.deleteFilmBy(id);
        log.info(String.format("FilmController: Remove film with id: %d.", id));
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(name = "count", defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }
}
