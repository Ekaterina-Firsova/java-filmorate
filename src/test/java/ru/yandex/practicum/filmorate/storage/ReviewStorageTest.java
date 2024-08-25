package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.builder.TestDataBuilder;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.rowmappers.ReviewRowMapper;

import java.util.Collection;

@JdbcTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({ReviewDbStorage.class, ReviewRowMapper.class})
@Transactional
public class ReviewStorageTest {

    private static final Long REVIEW_ID_START = 2L;
    private final ReviewDbStorage reviewDbStorage;
    private final JdbcTemplate jdbc;

    @BeforeEach
    public void resetSequences() {
        jdbc.execute("ALTER TABLE reviews ALTER COLUMN review_id RESTART WITH " + REVIEW_ID_START);
    }

    @Test
    public void whenSaveNewReviewThenReviewReturnWithId2() {
        Review reviewToSave = TestDataBuilder.buildReview();
        reviewDbStorage.save(reviewToSave);
        Review createdReview = reviewDbStorage.findById(2L).orElse(null);

        Assertions.assertNotNull(createdReview, "Отзыв не должен быть пустым");
        Assertions.assertAll(() -> {
            Assertions.assertEquals(2L, createdReview.getReviewId(), "Некорректный идентификатор отзыва");
            Assertions.assertEquals(reviewToSave.getContent(), createdReview.getContent(), "Некорректное содержание");
            Assertions.assertTrue(createdReview.getIsPositive(), "Некорректный тип отзыва");
            Assertions.assertEquals(reviewToSave.getUserId(), createdReview.getUserId(), "Некорректный пользователь");
            Assertions.assertEquals(reviewToSave.getFilmId(), createdReview.getFilmId(), "Некорректный фильм");
            Assertions.assertEquals(0, createdReview.getUseful(), "Некорректная оценка");
        });
    }

    @Test
    public void whenUpdateReviewWithId1ThenReturnUpdatedReview() {
        Review reviewToUpdate = TestDataBuilder.buildUpdateReview();
        reviewDbStorage.update(reviewToUpdate);
        Review updatedReview = reviewDbStorage.findById(1L).orElse(null);

        Assertions.assertNotNull(updatedReview, "Отзыв не должен быть пустым");
        Assertions.assertAll(() -> {
            Assertions.assertEquals(1L, updatedReview.getReviewId(), "Некорректный идентификатор отзыва");
            Assertions.assertEquals(reviewToUpdate.getContent(), updatedReview.getContent(), "Некорректное содержание");
            Assertions.assertFalse(updatedReview.getIsPositive(), "Некорректный тип отзыва");
            Assertions.assertEquals(0, updatedReview.getUseful(), "Некорректная оценка");
        });
    }

    @Test
    public void whenUpdateReviewWithUsefulThenScopeReset() {
        Assertions.assertTrue(reviewDbStorage.isLikeExist(1L, 2L),
                "В таблице должны быть данные о лайке");

        Review reviewToUpdate = TestDataBuilder.buildUpdateReview();
        reviewDbStorage.update(reviewToUpdate);
        Review updatedReview = reviewDbStorage.findById(1L).orElse(null);

        Assertions.assertNotNull(updatedReview, "Отзыв не должен быть пустым");
        Assertions.assertFalse(reviewDbStorage.isLikeExist(updatedReview.getReviewId(), 2L),
                "После обновления отзыва данных о лайках и кто их поставил быть не должно");
    }

    @Test
    public void whenDeleteReviewWithId1ThenReturnNull() {
        Assertions.assertTrue(reviewDbStorage.isExist(1L), "В таблице должен быть отзыв");
        reviewDbStorage.delete(1L);
        Assertions.assertFalse(reviewDbStorage.isExist(1L), "В таблице не должно быть отзыва после удаления");
    }

    @Test
    public void whenFindAllReviewByFilmIdThenReturnListSize2() {
        Review reviewToSave = TestDataBuilder.buildReview();
        reviewToSave.setFilmId(1L);
        reviewDbStorage.save(reviewToSave);

        Collection<Review> reviews = reviewDbStorage.findAllByFilmId(1L, 2);
        Assertions.assertEquals(2, reviews.size(), "Некорректное количество отзывов в массиве");
    }

    @Test
    public void whenReviewWithId1LikeThenUsefulReturn2() {
        reviewDbStorage.addLike(1L, 1L);
        Review review = reviewDbStorage.findById(1L).orElse(null);
        Assertions.assertNotNull(review, "Отзыв не должен быть пустым");
        Assertions.assertEquals(2, review.getUseful(), "После лайка отзыв должен иметь оценку 2");
    }

    @Test
    public void whenReviewWithId1DislikeThenUsefulReturnMinus1() {
        reviewDbStorage.addDislike(1L, 1L);
        Review review = reviewDbStorage.findById(1L).orElse(null);
        Assertions.assertNotNull(review, "Отзыв не должен быть пустым");
        Assertions.assertEquals(-1, review.getUseful(), "После лайка отзыв должен иметь отрицательную оценку");
    }

    @Test
    public void whenReviewWithId1RemoveLikeThenReturnUseful0() {
        reviewDbStorage.removeLike(1L, 1L);
        Review review = reviewDbStorage.findById(1L).orElse(null);
        Assertions.assertNotNull(review, "Отзыв не должен быть пустым");
        Assertions.assertEquals(0, review.getUseful(), "После удаления лайка значение должно быть 0");
    }

    @Test
    public void whenReviewWithId1RemoveDislikeThenReturnUseful0() {
        reviewDbStorage.addDislike(1L, 1L);
        reviewDbStorage.removeDislike(1L, 1L);
        Review review = reviewDbStorage.findById(1L).orElse(null);
        Assertions.assertNotNull(review, "Отзыв не должен быть пустым");
        Assertions.assertEquals(0, review.getUseful(), "После удаления дизлайка значение должно быть 0");
    }
}
