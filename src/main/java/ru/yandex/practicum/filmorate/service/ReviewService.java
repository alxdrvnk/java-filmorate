package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dao.ReviewLikeDao;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDao reviewDao;
    private final ReviewLikeDao reviewLikeDao;
    private final UserService userService;
    private final FilmService filmService;

    public Review create(Review review) {
        userService.getUserBy(review.getUserId());
        filmService.getFilmBy(review.getFilmId());

        if (review.getUseful() != 0){
            review.withUseful(0);
        }

        return reviewDao.create(review);
    }

    public Review update(Review review) {
        Review fromDb = get(review.getReviewId());

        if (review.getUseful() < 0 || fromDb.getUseful() - review.getUseful() > 1
                || review.getUseful() - fromDb.getUseful() > 1
        ) {
            review = review.withUseful(fromDb.getUseful());
        }

        return reviewDao.update(review);
    }

    public void delete(Long id) {
        reviewDao.deleteBy(id);
    }

    public Review get(Long id) {
        return reviewDao.getBy(id).orElseThrow(() -> new FilmorateNotFoundException("Отзыв не найден"));
    }

    public List<Review> getByFilm(Long filmId, Integer count) {
        return reviewDao.getByFilm(filmId, count);
    }

    public void addLike(Long id, Long userId) {
        Review review = get(id);
        boolean isAdd = reviewLikeDao.addLike(id, userId);

        if (isAdd){
            int useful = review.getUseful() + 1;
            reviewDao.update(review.withUseful(useful));
        }
    }

    public void addDislike(Long id, Long userId) {
        Review review = get(id);
        boolean isAdd = reviewLikeDao.addDislike(id, userId);

        if (isAdd){
            int useful = review.getUseful() - 1;
            reviewDao.update(review.withUseful(useful));
        }
    }

    public void removeLike(Long id, Long userId) {
        Review review = get(id);
        boolean isRemove = reviewLikeDao.removeLike(id, userId);

        if (isRemove){
            int useful = review.getUseful() - 1;
            reviewDao.update(review.withUseful(useful));
        }
    }

    public void removeDislike(Long id, Long userId) {
        Review review = get(id);
        boolean isRemove = reviewLikeDao.removeDislike(id, userId);

        if (isRemove){
            int useful = review.getUseful() + 1;
            reviewDao.update(review.withUseful(useful));
        }
    }
}
