package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * Represents a film genre in the Filmorate application.
 * <p>
 * The {@code Genre} class defines the type or category of a film, such as Comedy, Drama, Animation,
 * etc. Each genre is uniquely identified by an ID and has a unique name.
 */
@Data
@Builder
public class Genre {

  private static final int MAX_NAME_LENGTH = 100;

  private long id;

  @NotBlank(message = "Name should not be empty or blank.")
  @Size(max = MAX_NAME_LENGTH, message = "Genre name should not exceed "
      + MAX_NAME_LENGTH + " characters.")
  private String name;

}
