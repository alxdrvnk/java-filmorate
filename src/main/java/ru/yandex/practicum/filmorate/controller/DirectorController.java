package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    // GET /directors - Список всех режиссёров
    @GetMapping
    public List<Director> getAllDirectors() {
        return directorService.getAllDirectors();
    }

    // GET /directors/{id}- Получение режиссёра по id
    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable Integer id) {
        return directorService.getDirectorById(id);
    }

    // POST /directors - Создание режиссёра
    @PostMapping
    public Director createDirector(@RequestBody Director director) {
        return directorService.createDirector(director);
    }

    // PUT /directors - Изменение режиссёра
    @PutMapping
    public Director updateDirector(@RequestBody Director director) {
        return directorService.updateDirector(director);
    }

    // DELETE /directors/{id} - Удаление режиссёра
    @DeleteMapping("/{id}")
    public void deleteDirectorById(@PathVariable Integer id) {
        directorService.deleteDirectorById(id);
    }

    //GET /films/director/{directorId}?sortBy=[year,likes]  - добавить в FilmController
    //Возвращает список фильмов режиссера отсортированных по количеству лайков или году выпуска.
}
