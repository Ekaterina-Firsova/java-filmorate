package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewRequest;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

/**
 * A service interface for managing reviews in the application.
 * <p>
 * This service provides methods for creating, updating, deleting, and retrieving reviews, as well
 * as managing user interactions such as likes and dislikes for each review.
 * <ul>
 *   <li>{@link #getReviewsByFilmId(Long, Integer)} - Retrieves a collection of reviews for a specified film.</li>
 *   <li>{@link #getReviewById(Long)} - Retrieves a review by its ID.</li>
 *   <li>{@link #saveReview(ReviewRequest)} - Saves a new review to the storage.</li>
 *   <li>{@link #updateReview(ReviewRequest)} - Updates an existing review in the storage.</li>
 *   <li>{@link #removeReview(Long)} - Deletes a review from the storage.</li>
 *   <li>{@link #addReviewLike(Long, Long)} - Adds a like to a review by a specified user.</li>
 *   <li>{@link #addReviewDislike(Long, Long)} - Adds a dislike to a review by a specified user.</li>
 *   <li>{@link #removeLike(Long, Long)} - Removes a like from a review by a specified user.</li>
 *   <li>{@link #removeDislike(Long, Long)} - Removes a dislike from a review by a specified user.</li>
 * </ul>
 *
 * @see ReviewDto
 * @see ReviewRequest
 * @see EventType
 * @see Operation
 */
public interface ReviewService {

  Collection<ReviewDto> getReviewsByFilmId(Long filmId, Integer count);

  ReviewDto getReviewById(Long reviewId);

  ReviewDto saveReview(ReviewRequest request);

  ReviewDto updateReview(ReviewRequest request);

  void removeReview(Long reviewId);

  ReviewDto addReviewLike(Long reviewId, Long userId);

  ReviewDto addReviewDislike(Long reviewId, Long userId);

  void removeLike(Long reviewId, Long userId);

  void removeDislike(Long reviewId, Long userId);
}
