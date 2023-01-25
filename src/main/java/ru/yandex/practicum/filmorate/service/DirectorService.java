package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorService {
    private final DirectorDao directorDao;
    private final FilmDao filmDao;
    public List<Director> getAllDirectors() {
        log.info("Получен запрос на список всех режиссёров");
        List<Director> directors = directorDao.getAllDirectors();
        return directors;

    }

    public Director getDirectorById(Integer id) {
        log.info("Получен запрос на получение режиссёра по id = " + id);
        return directorDao.getDirectorById(id);
    }

    public Director createDirector(Director director) {
        log.info("Получен запрос на добавление режиссёра");
       return directorDao.createDirector(director);
    }

    public Director updateDirector(Director director) {
        log.info("Получен запрос на обновление режиссёра с id = " + director.getId());
        directorDao.updateDirector(director);
        return null;
    }

    public void deleteDirectorById(Integer id) {
        log.info("Получен запрос на удаление режиссёра по id = " + id);
        directorDao.deleteDirectorById(id);
    }
}
