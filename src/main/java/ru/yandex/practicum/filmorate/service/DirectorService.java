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
        directorDao.getAllDirectors();
        return null;
    }

    public Director getDirectorById(Integer id) {
        directorDao.getDirectorById(id);
        return null;
    }

    public Director createDirector(Director director) {
        directorDao.createDirector(director);
        return null;
    }

    public Director updateDirector(Director director) {
        directorDao.updateDirector(director);
        return null;
    }

    public void deleteDirectorById(Integer id) {
    directorDao.deleteDirectorById(id);
    }
}
