package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorService {
    private final DirectorDao directorDao;

    public List<Director> getAllDirectors() {
        log.info("Received a request for a list of all directors");
        List<Director> directors = directorDao.getAllDirectors();
        return directors;

    }

    public Director getDirectorById(Integer id) {
        log.info("A request was received to get a director by id = " + id);
        return directorDao.getDirectorById(id).orElseThrow(() -> new FilmorateNotFoundException(
                String.format("Could not find director with id = "+ id + "he is not in the list")));
    }

    public Director createDirector(Director director) {
        log.info("Director request received");
        return directorDao.createDirector(director);
    }

    public Director updateDirector(Director director) {
        log.info("Request received to update director with id = " + director.getId());
        return directorDao.updateDirector(director);
    }

    public void deleteDirectorById(Integer id) {
        log.info("A request was received to delete a director by id = " + id);
        directorDao.deleteDirectorById(id);
    }
}
