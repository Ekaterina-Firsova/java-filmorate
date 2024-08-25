package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewRequest;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@RestController
@RequestMapping("/reviews")
@Validated
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public Collection<ReviewDto> getReviewsByFilmId(@RequestParam(required = false) Long filmId,
                                                    @RequestParam(defaultValue = "10") @Positive Integer count) {
        return reviewService.getReviewsByFilmId(filmId, count);
    }

    @GetMapping("/{id}")
    public ReviewDto getReviewById(@PathVariable("id") @Positive @NotNull Long id) {
        return reviewService.getReviewById(id);
    }

    @PostMapping
    public ReviewDto saveReview(@Valid @RequestBody ReviewRequest request) {
        return reviewService.saveReview(request);
    }

    @PutMapping
    public ReviewDto updateReview(@Valid @RequestBody ReviewRequest request) {
        return reviewService.updateReview(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable("id") @Positive @NotNull Long id) {
        reviewService.removeReview(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public ReviewDto addReviewLike(@PathVariable("id") @Positive @NotNull Long id,
                                   @PathVariable("userId") @Positive @NotNull Long userId) {
        return reviewService.addReviewLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ReviewDto addReviewDislike(@PathVariable("id") @Positive @NotNull Long id,
                                   @PathVariable("userId") @Positive @NotNull Long userId) {
        return reviewService.addReviewDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") @Positive @NotNull Long id,
                           @PathVariable("userId") @Positive @NotNull Long userId) {
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable("id") @Positive @NotNull Long id,
                           @PathVariable("userId") @Positive @NotNull Long userId) {
        reviewService.removeDislike(id, userId);
    }
}
