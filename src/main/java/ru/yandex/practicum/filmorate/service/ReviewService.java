package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.aspects.HandleFilmorateCreateEvent;
import ru.yandex.practicum.filmorate.aspects.HandleFilmorateDeleteEvent;
import ru.yandex.practicum.filmorate.aspects.HandleFilmorateEvent;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dao.ReviewLikeDao;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.utils.FilmorateEventOperation;
import ru.yandex.practicum.filmorate.utils.FilmorateEventType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDao reviewDao;
    private final ReviewLikeDao reviewLikeDao;
    private final UserService userService;
    private final FilmService filmService;

    @HandleFilmorateCreateEvent(eventType = FilmorateEventType.REVIEW, eventOperation = FilmorateEventOperation.ADD)
    public Review create(Review review) {
        userService.getUserBy(review.getUserId());
        filmService.getFilmBy(review.getFilmId());

        if (review.getUseful() != 0) {
            review.withUseful(0);
        }

        return reviewDao.create(review);
    }

    @HandleFilmorateCreateEvent(eventType = FilmorateEventType.REVIEW, eventOperation = FilmorateEventOperation.UPDATE)
    public Review update(Review review) {
        Review fromDb = get(review.getReviewId());

        if (review.getUseful() < 0 || fromDb.getUseful() - review.getUseful() > 1
                || review.getUseful() - fromDb.getUseful() > 1
        ) {
            review = review.withUseful(fromDb.getUseful());
        }

        return reviewDao.update(review);
    }

    @HandleFilmorateDeleteEvent(eventType = FilmorateEventType.REVIEW, eventOperation = FilmorateEventOperation.REMOVE)
    public void delete(Long id) {
        reviewDao.deleteBy(id);
    }

    public Review get(Long id) {
        return reviewDao.getBy(id).orElseThrow(() -> new FilmorateNotFoundException("Отзыв не найден"));
    }

    public List<Review> getByFilm(Long filmId, Integer count) {
        return reviewDao.getByFilm(filmId, count);
    }

    @HandleFilmorateEvent(eventType = FilmorateEventType.LIKE, eventOperation = FilmorateEventOperation.ADD)
    public void addLike(Long id, Long userId) {
        Review review = get(id);

        boolean isRemoved = reviewLikeDao.removeDislike(id, userId);
        if (isRemoved) {
            int useful = review.getUseful() + 1;
            review = review.withUseful(useful);
        }

        boolean isAdd = reviewLikeDao.addLike(id, userId);
        if (isAdd) {
            int useful = review.getUseful() + 1;
            review = review.withUseful(useful);
        }

        reviewDao.update(review);
    }

    @HandleFilmorateEvent(eventType = FilmorateEventType.LIKE, eventOperation = FilmorateEventOperation.REMOVE)
    public void addDislike(Long id, Long userId) {
        Review review = get(id);

        boolean isRemoved = reviewLikeDao.removeLike(id, userId);
        if (isRemoved) {
            int useful = review.getUseful() - 1;
            review = review.withUseful(useful);
        }
        boolean isAdd = reviewLikeDao.addDislike(id, userId);

        if (isAdd) {
            int useful = review.getUseful() - 1;
            review = review.withUseful(useful);
        }

        reviewDao.update(review);
    }

    @HandleFilmorateEvent(eventType = FilmorateEventType.LIKE, eventOperation = FilmorateEventOperation.UPDATE)
    public void removeLike(Long id, Long userId) {
        Review review = get(id);

        boolean isRemoved = reviewLikeDao.removeLike(id, userId);
        if (isRemoved) {
            int useful = review.getUseful() - 1;
            reviewDao.update(review.withUseful(useful));
        }
    }

    @HandleFilmorateEvent(eventType = FilmorateEventType.LIKE, eventOperation = FilmorateEventOperation.UPDATE)
    public void removeDislike(Long id, Long userId) {
        Review review = get(id);

        boolean isRemoved = reviewLikeDao.removeDislike(id, userId);
        if (isRemoved) {
            int useful = review.getUseful() + 1;
            reviewDao.update(review.withUseful(useful));
        }
    }
}
