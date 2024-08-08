package ru.yandex.practicum.filmorate.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.MpaRating;

/**
 * Data transfer Object representing a MpaRating.
 *
 * @see MpaRating
 */
@Data
@Builder
public class MpaRatingDto {

  private static final int MAX_NAME_LENGTH = 100;

  private Long id;

  @NotBlank(message = "Name should not be empty.")
  @Size(max = MAX_NAME_LENGTH, message = "Mpa rating name should not exceed "
      + MAX_NAME_LENGTH + " characters.")
  private String name;
}
