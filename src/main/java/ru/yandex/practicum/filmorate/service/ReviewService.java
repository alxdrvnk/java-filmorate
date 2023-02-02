package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
    private final EventsService eventsService;

    public Review create(Review review) {
        if (review.getUseful() != 0) {
            review.withUseful(0);
        }

        review = reviewDao.create(review);

        eventsService.create(review.getUserId(),
                review.getReviewId(),
                FilmorateEventType.REVIEW,
                FilmorateEventOperation.ADD);

        return review;
    }

    public Review update(Review review) {
        Review fromDb = get(review.getReviewId());

        if (review.getUseful() < 0 || fromDb.getUseful() - review.getUseful() > 1
                || review.getUseful() - fromDb.getUseful() > 1
        ) {
            review = review.withUseful(fromDb.getUseful());
        }
        review = reviewDao.update(review);

        eventsService.create(review.getUserId(),
                review.getReviewId(),
                FilmorateEventType.REVIEW,
                FilmorateEventOperation.UPDATE);

        return  review;
    }

    public void delete(Long id) {
        Review review = get(id);

        reviewDao.deleteBy(id);

        eventsService.create(review.getUserId(),
                review.getReviewId(),
                FilmorateEventType.REVIEW,
                FilmorateEventOperation.REMOVE);
    }

    public Review get(Long id) {
        return reviewDao.getBy(id).orElseThrow(() -> new FilmorateNotFoundException("Отзыв не найден"));
    }

    public List<Review> getByFilmOrDefault(Long filmId, Integer count) {
        return reviewDao.getByFilmOrDefault(filmId, count);
    }

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

    public void removeLike(Long id, Long userId) {
        Review review = get(id);

        boolean isRemoved = reviewLikeDao.removeLike(id, userId);
        if (isRemoved) {
            int useful = review.getUseful() - 1;
            reviewDao.update(review.withUseful(useful));

        }
    }

    public void removeDislike(Long id, Long userId) {
        Review review = get(id);

        boolean isRemoved = reviewLikeDao.removeDislike(id, userId);
        if (isRemoved) {
            int useful = review.getUseful() + 1;
            reviewDao.update(review.withUseful(useful));
        }
    }
}
