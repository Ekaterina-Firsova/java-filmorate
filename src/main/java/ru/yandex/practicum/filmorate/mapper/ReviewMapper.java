package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewRequest;
import ru.yandex.practicum.filmorate.model.Review;

@Slf4j
@UtilityClass
public class ReviewMapper {
    public Review mapToReview(ReviewRequest request) {
        log.info("ReviewRequest в маппер: {}", request);
        Review review = Review.builder()
                .reviewId(request.getReviewId())
                .content(request.getContent())
                .isPositive(request.getIsPositive())
                .userId(request.getUserId())
                .filmId(request.getFilmId())
                .build();
        log.info("Review из маппера: {}", review);
        return review;
    }

    public ReviewDto mapToReviewDto(Review review) {
        log.info("Review в маппер: {}", review);
        ReviewDto reviewDto = ReviewDto.builder()
                .reviewId(review.getReviewId())
                .content(review.getContent())
                .isPositive(review.getIsPositive())
                .userId(review.getUserId())
                .filmId(review.getFilmId())
                .useful(review.getUseful())
                .build();
        log.info("ReviewDto из маппера: {}", reviewDto);
        return reviewDto;
    }
}
