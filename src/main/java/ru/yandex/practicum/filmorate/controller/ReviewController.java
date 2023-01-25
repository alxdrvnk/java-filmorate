package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
        log.info(String.format("ReviewController: create Review request. Data: %s", review));
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.info(String.format("ReviewController: update Review request. Data: %s", review));
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
        log.info(String.format("ReviewController: delete Review with id %d", id));
    }

    @GetMapping("/{id}")
    public Review findById(@PathVariable Long id) {
        return reviewService.get(id);
    }

    @GetMapping
    public List<Review> findByFilm(@RequestParam(required = false) Long filmId,
                                   @RequestParam(required = false, defaultValue = "10") Integer count) {
        return reviewService.getByFilm(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addLike(id, userId);
        log.info(String.format("ReviewController: User with id %d add like to review with id %d", userId, id));
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addDislike(id, userId);
        log.info(String.format("ReviewController: User with id %d add dislike to review with id %d", userId, id));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeLike(id, userId);
        log.info(String.format("ReviewController: User with id %d remove like from review with id %d", userId, id));
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeDislike(id, userId);
        log.info(String.format("ReviewController: User with id %d remove dislike from review with id %d", userId, id));
    }
}
