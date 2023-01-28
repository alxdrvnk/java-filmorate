package ru.yandex.practicum.filmorate.dao;

public interface ReviewLikeDao {
    boolean addLike(Long reviewId, Long userId);

    boolean addDislike(Long reviewId, Long userId);

    boolean removeLike(Long reviewId, Long userId);

    boolean removeDislike(Long reviewId, Long userId);
}
