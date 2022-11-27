package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final List<Film> filmStorage;

    public FilmController() {
        filmStorage = new ArrayList<>();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Film controller get method POST. Data: " + film);
        filmStorage.add(film);
        return film;
    }

    @GetMapping
    public List<Film> findAll() {
        return filmStorage;
    }

    @GetMapping("/{id}")
    public Film findFilmBy(@PathVariable("id") Integer id) {
        return filmStorage.get(id);
    }
}
