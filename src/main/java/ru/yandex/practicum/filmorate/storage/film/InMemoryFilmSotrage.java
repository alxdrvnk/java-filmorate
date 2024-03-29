package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component("inMemoryFilmStorage")
@Slf4j
public class InMemoryFilmSotrage implements FilmStorage {


    private final HashMap<Long, Film> films = new HashMap<>();

    private Long id = 1L;

    private Long getNextId() {
        return id++;
    }

    @Override
    public Film create(Film film) {
        Long id = getNextId();
        Film newFilm = film.withId(id);
        films.put(id, newFilm);
        return newFilm;
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new FilmorateNotFoundException(String.format("Фильм с id: %d не найден.", film.getId()));
        }
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film get(Long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new FilmorateNotFoundException(String.format("Фильм с id: %d не найден.", id));
        }
    }

    @Override
    public List<Film> getPopular(int count) {
        return films.values().stream()
                .sorted(((o1, o2) -> (int) (o2.getRate() - o1.getRate())))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film addLike(Film film) {
        Film newFilm = film.withRate(film.getRate() + 1);
        films.put(film.getId(), newFilm);
        return newFilm;
    }

    @Override
    public Film removeLike(Film film) {
        Film newFilm = film.withRate(film.getRate() - 1);
        films.put(film.getId(), newFilm);
        return newFilm;
    }
}
