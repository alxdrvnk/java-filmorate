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
        log.info(String.format("FilmController: получен POST запрос. Data: %s", film));
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info(String.format("FilmController: получен PUT запрос. Data: %s)", film));
        return filmService.update(film);
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film findFilmBy(@PathVariable("id") Integer id) {
        return filmService.getFilmBy(id);
    }
}
