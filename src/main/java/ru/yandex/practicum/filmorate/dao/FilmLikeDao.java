package ru.yandex.practicum.filmorate.dao;

import java.util.List;
import java.util.Map;

public interface FilmLikeDao {

    boolean addFilmLike(Long filmId, Long userId);

    boolean removeFilmLike(Long filmId, Long userId);

    Map<Long, List<Long>> getSameLikesByUser(Long userId);

}
