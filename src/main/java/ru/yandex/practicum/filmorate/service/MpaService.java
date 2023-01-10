package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaDao storage;

    public List<Mpa> getAll() {
        return storage.getAll();
    }

    public Mpa getById(Long id) {
        return storage.getBy(id).orElseThrow(() -> new FilmorateNotFoundException("Рейтинг не найден."));
    }
}
