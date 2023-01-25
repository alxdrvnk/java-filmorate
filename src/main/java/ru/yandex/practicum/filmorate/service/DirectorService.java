package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmorateValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorService {
    private final DirectorDao directorDao;

    public List<Director> getAllDirectors() {
        log.info("Получен запрос на список всех режиссёров");
        List<Director> directors = directorDao.getAllDirectors();
        return directors;

    }

    public Director getDirectorById(Integer id) {
        log.info("Получен запрос на получение режиссёра по id = " + id);
        Director directorGetById = directorDao.getDirectorById(id);
        if (Objects.isNull(directorGetById)) {
            log.warn("Ошибка поиска режиссера по id= " + id);
            throw new FilmorateNotFoundException("Не удалось найти режиссера с id " + id + ", его нет в списке");
        }
        return directorGetById;
    }

    public Director createDirector(Director director) {
        log.info("Получен запрос на добавление режиссёра");
        if (director.getName().isBlank()){
            throw new FilmorateValidationException("Имя режисера не может быть пустым");
        } else {
            return directorDao.createDirector(director);
        }
    }

    public Director updateDirector(Director director) {
        log.info("Получен запрос на обновление режиссёра с id = " + director.getId());
        Director directorUpdate = directorDao.updateDirector(director);
        if (Objects.isNull(directorUpdate)) {
            log.warn("Ошибка обновления режиссера");
            throw new FilmorateNotFoundException("Не удалось обновить режиссера, его нет в списке");
        }
        return directorUpdate;
    }

    public void deleteDirectorById(Integer id) {
        log.info("Получен запрос на удаление режиссёра по id = " + id);
        directorDao.deleteDirectorById(id);
    }
}
