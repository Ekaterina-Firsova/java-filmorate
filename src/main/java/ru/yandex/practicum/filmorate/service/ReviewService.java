package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewRequest;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.InvalidDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventService eventService;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage,
                         @Qualifier("userDbStorage") UserStorage userStorage,
                         @Qualifier("filmDbStorage") FilmStorage filmStorage,
                         EventService eventService) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventService = eventService;
    }

    public Collection<ReviewDto> getReviewsByFilmId(Long filmId, Integer count) {
        List<ReviewDto> reviews = reviewStorage.findAllByFilmId(filmId, count)
                .stream()
                .map(ReviewMapper::mapToReviewDto)
                .toList();
        log.info("Список отзывов {} для фильма {} в количестве {}", reviews, filmId, count);
        return reviews;
    }

    public ReviewDto getReviewById(Long reviewId) {
        ReviewDto reviewDto = ReviewMapper.mapToReviewDto(getReview(reviewId));
        log.info("Отзыв с id {} = {}", reviewId, reviewDto);
        return reviewDto;
    }

    public ReviewDto saveReview(ReviewRequest request) {
        log.info("Запрос на сохранение отзыва: {}", request);
        Review newReview = ReviewMapper.mapToReview(request);
        validateFilmId(newReview.getFilmId());
        validateUserId(request.getUserId());
        newReview = reviewStorage.save(newReview);
        log.info("Отзыв сохранен: {}", newReview);
        eventService.logEvent(newReview.getUserId(), newReview.getReviewId(), EventType.REVIEW, Operation.ADD);
        return ReviewMapper.mapToReviewDto(newReview);
    }

    public ReviewDto updateReview(ReviewRequest request) {
        log.info("Запрос на обновление отзыва: {}", request);
        Review newReview = ReviewMapper.mapToReview(request);
        if (newReview.getReviewId() == null) {
            log.error("Отсутствует идентификатор отзыва при обновлении");
            throw new InvalidDataException("Не был передан идентификатор отзыва");
        }
        Review oldReview = getReview(newReview.getReviewId());
        if (!newReview.equals(oldReview)) {
            log.error("Некорректный входные параметры, запрос: {}, отзыв в БД: {}", newReview, oldReview);
            throw new InvalidDataException("Попытка изменить отзыв с невалидными входными параметрами");
        }
        int newUseful = 0;
        newReview.setUseful(newUseful);
        newReview = reviewStorage.update(newReview);
        log.info("Отзыв обновлен: {}", newReview);
        eventService.logEvent(newReview.getUserId(), newReview.getReviewId(), EventType.REVIEW, Operation.UPDATE);
        return ReviewMapper.mapToReviewDto(newReview);
    }

    public void removeReview(Long reviewId) {
        log.info("Запрос на удаление отзыва с id: {}", reviewId);
        Review current = getReview(reviewId);
        reviewStorage.delete(current.getReviewId());
        eventService.logEvent(current.getUserId(), current.getReviewId(), EventType.REVIEW, Operation.REMOVE);
    }

    public ReviewDto addReviewLike(Long reviewId, Long userId) {
        return addReviewReaction(reviewId, userId, Boolean.TRUE);
    }

    public ReviewDto addReviewDislike(Long reviewId, Long userId) {
        return addReviewReaction(reviewId, userId, Boolean.FALSE);
    }

    public void removeLike(Long reviewId, Long userId) {
        removeReaction(reviewId, userId, Boolean.TRUE);
    }

    public void removeDislike(Long reviewId, Long userId) {
        removeReaction(reviewId, userId, Boolean.FALSE);
    }

    private ReviewDto addReviewReaction(Long reviewId, Long userId, Boolean isLike) {
        log.info("Запрос на добавление оценки isLike = {} отзыву с id {} от пользователя {}", isLike, reviewId, userId);
        getReview(reviewId);
        validateUserId(userId);
        checkLikeDuplicate(reviewId, userId, isLike);
        if (isLike) {
            reviewStorage.addLike(reviewId, userId);
        } else {
            reviewStorage.addDislike(reviewId, userId);
        }
        Review review = getReview(reviewId);
        log.info("Отзыву {} добавлена оценка isLike = {} от пользователя {}", review, isLike, userId);
        return ReviewMapper.mapToReviewDto(review);
    }

    private void removeReaction(Long reviewId, Long userId, Boolean isLike) {
        log.info("Запрос на удаление оценки для отзыва {} от пользователя {}", reviewId, userId);
        getReview(reviewId);
        validateUserId(userId);
        checkEmptyLike(reviewId, userId);
        if (isLike) {
            reviewStorage.removeLike(reviewId, userId);
        } else {
            reviewStorage.removeDislike(reviewId, userId);
        }
    }

    private Review getReview(Long id) {
        return reviewStorage.findById(id).orElseThrow(() -> {
            log.error("Такого отзыва нет {}", id);
            return new NotFoundException(String.format("Отзыва с идентификатором = '%s' не найдено", id));
        });
    }

    private void validateFilmId(Long id) {
        log.info("Проверка, есть ли фильм с id {}", id);
        if (id == null || !filmStorage.isExist(id)) {
            log.error("Фильма с id = {} не существует", id);
            throw new NotFoundException(String.format("Фильма с идентификатором = '%s' не найдено", id));
        }
    }

    private void validateUserId(Long id) {
        log.info("Проверка, есть ли пользователь с id {}", id);
        if (id == null || !userStorage.isExist(id)) {
            log.error("Пользователя с id = {} не существует", id);
            throw new NotFoundException(String.format("Пользователя с идентификатором = '%s' не найдено", id));
        }
    }

    private void checkLikeDuplicate(Long reviewId, Long userId, Boolean isUseful) {
        if (reviewStorage.isLikeAndUsefulExist(reviewId, userId, isUseful)) {
            log.error("Попытка пользователем {} оценить отзыв {} повторно", userId, reviewId);
            throw new DuplicatedDataException("Нельзя добавить оценку отзыву второй раз");
        }
    }

    private void checkEmptyLike(Long reviewId, Long userId) {
        if (!reviewStorage.isLikeExist(reviewId, userId)) {
            log.error("Попытка пользователем {} удалить несуществующий отзыв {}", userId, reviewId);
            throw new DuplicatedDataException("Нельзя удалить пустую оценку");
        }
    }
}
