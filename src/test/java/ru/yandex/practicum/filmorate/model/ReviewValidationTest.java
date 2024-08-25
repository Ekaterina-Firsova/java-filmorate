package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dto.ReviewRequest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ReviewValidationTest {

    @Autowired
    private Validator validator;
    private ReviewRequest review;

    @BeforeEach
    public void setUp() {
        review = ReviewRequest.builder()
                .reviewId(1L)
                .content("Content")
                .isPositive(Boolean.TRUE)
                .userId(1L)
                .filmId(1L)
                .build();
    }

    @Test
    public void checkSuccessReviewValidation() {
        Set<ConstraintViolation<ReviewRequest>> result = validator.validate(review);
        assertTrue(result.isEmpty(), "Ошибки по валидации быть не должно");
    }

    @Test
    public void whenContentIsNullThenValidationIsFailed() {
        review.setContent("");
        checkAssert("content", "Детализация отзыва должна быть заполнена");
    }

    @Test
    public void whenContentLengthIs1001ThenValidationIsFailed() {
        review.setContent(RandomStringUtils.randomAlphabetic(1001));
        checkAssert("content", "Максимальный размер отзыва 1000 символов");
    }

    @Test
    public void whenIsPositiveIsNullThenValidationIsFailed() {
        review.setIsPositive(null);
        checkAssert("isPositive", "Тип отзыва должен быть заполнен");
    }

    @Test
    public void whenUserIdIsNullThenValidationIsFailed() {
        review.setUserId(null);
        checkAssert("userId", "Идентификатор пользователя не должен быть пустым");
    }

    @Test
    public void whenFilmIdIsNullThenValidationIsFailed() {
        review.setFilmId(null);
        checkAssert("filmId", "Идентификатор фильма не должен быть пустым");
    }

    private void checkAssert(String field, String errorMessage) {
        List<ConstraintViolation<ReviewRequest>> result = List.copyOf(validator.validate(review));
        assertEquals(1, result.size(), "Отсутствует ошибка по валидации поля");

        ConstraintViolation<ReviewRequest> validationResult = result.getFirst();

        assertEquals(field, validationResult.getPropertyPath().toString());
        assertEquals(errorMessage, validationResult.getMessage());
    }
}
