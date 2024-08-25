package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage extends Storage<Review> {

    boolean isLikeAndUsefulExist(Long reviewId, Long userId, Boolean isUseful);

    boolean isLikeExist(Long reviewId, Long userId);

    void addLike(Long reviewId, Long userId);

    void addDislike(Long reviewId, Long userId);

    void removeLikes(Long reviewId);

    void removeLike(Long reviewId, Long userId);

    void removeDislike(Long reviewId, Long userId);

    Collection<Review> findAllByFilmId(Long filmId, Integer count);
}
