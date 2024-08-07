package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validator.DateAfter;


/**
 * Represents a movie in the Filmorate application.
 */
@Data
@EqualsAndHashCode(of = {"name", "releaseDate"})
@Builder
public class Film {

  static final String MIN_DATE = "1895-12-28";
  static final int MAX_DESCRIPTION_SIZE = 200;

  /**
   * The unique identifier of the film.
   */
  private Long id;

  /**
   * The name of the film. Must not be blank.
   */
  @NotBlank(message = "Name should not be empty.")
  private String name;

  /**
   * The description of the film. Must not be blank and must not exceed 200 characters.
   */
  @Size(max = MAX_DESCRIPTION_SIZE, message = "Description should not exceed "
      + MAX_DESCRIPTION_SIZE + " characters.")
  private String description;

  /**
   * The release date of the film. Must not be in the future and must not be before December 28,
   * 1895.
   */
  @NotNull(message = "ReleaseDate should not be null.")
  @PastOrPresent(message = "Release date should not be in future.")
  @DateAfter(after = MIN_DATE, message = "Release date should not be before " + MIN_DATE)
  private LocalDate releaseDate;

  /**
   * The duration of the film in minutes. Must be a positive number.
   */
  @Positive(message = "Duration must be a positive number.")
  private Long duration;

  /**
   * The MPA rating of the film.
   */
  private MpaRating mpa;

  /**
   * A set of genres associated with the film.
   */
  private final Set<Genre> gernres = new HashSet<Genre>();


  /**
   * A set of the user IDs representing the users who have liked this film. Each user can like the
   * film only once.
   */
  private final Set<Long> likes = new HashSet<>();

}
