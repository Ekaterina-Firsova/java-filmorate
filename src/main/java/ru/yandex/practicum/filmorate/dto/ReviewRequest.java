package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewRequest {
    private Long reviewId;
    @NotBlank(message = "Детализация отзыва должна быть заполнена")
    @Size(max = 1000, message = "Максимальный размер отзыва 1000 символов")
    private String content;
    @NotNull(message = "Тип отзыва должен быть заполнен")
    private Boolean isPositive;
    @NotNull(message = "Идентификатор пользователя не должен быть пустым")
    private Long userId;
    @NotNull(message = "Идентификатор фильма не должен быть пустым")
    private Long filmId;
}
