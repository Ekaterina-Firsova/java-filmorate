package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;

/**
 * Data transfer Object representing a Genre.
 * @see Genre
 */
@Data
@Builder
public class GenreDto {

  private static final int MAX_NAME_LENGTH = 100;

  private long id;

  @NotBlank(message = "Name should not be empty or blank.")
  @Size(max = MAX_NAME_LENGTH, message = "Genre name should not exceed "
      + MAX_NAME_LENGTH + " characters.")
  private String name;

}
