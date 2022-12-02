package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController() {
        filmService = new FilmService();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Film controller get method POST. Data: " + film);
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Film controller get PUT request. Data: " + film);
        return filmService.update(film);
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Film controller get method GET for all films");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film findFilmBy(@PathVariable("id") Integer id) {
        return filmService.getFilmBy(id);
    }
}
