package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDao extends FilmorateDao<Review> {

    List<Review> getByFilmOrDefault(Long filmId, Integer count);
}
