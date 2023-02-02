package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        log.info("ReviewController: create Review request. Data: {}", review);
        if (review.getUserId() < 1 || review.getFilmId() < 1){
            throw new FilmorateNotFoundException("ReviewController: User id: %d or Film id: %d is incorrect");
        }
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.info("ReviewController: update Review request. Data: {}", review);
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
        log.info("ReviewController: delete Review with id {}", id);
    }

    @GetMapping("/{id}")
    public Review findById(@PathVariable Long id) {
        return reviewService.get(id);
    }

    @GetMapping
    public List<Review> findByFilmOrDefault(@RequestParam(required = false) Long filmId,
                                            @RequestParam(defaultValue = "10") Integer count) {
        return reviewService.getByFilmOrDefault(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addLike(id, userId);
        log.info("ReviewController: User with id {} add like to review with id {}", userId, id);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addDislike(id, userId);
        log.info("ReviewController: User with id {} add dislike to review with id {}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeLike(id, userId);
        log.info("ReviewController: User with id {} remove like from review with id {}", userId, id);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeDislike(id, userId);
        log.info("ReviewController: User with id {} remove dislike from review with id {}", userId, id);
    }
}
