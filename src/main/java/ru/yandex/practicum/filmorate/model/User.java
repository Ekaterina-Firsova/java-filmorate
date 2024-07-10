package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents a user in the Filmorate application.
 */
@Data
@EqualsAndHashCode(of = {"email", "login", "birthday"})
@Builder
public class User {

  /**
   * The unique identifier of the film.
   */
  private Long id;

  /**
   * The email address of the user. Must not be blank and must be a valid email format.
   */
  @NotBlank(message = "Email must not be blank.")
  @Email(message = "Email should be correct format")
  private String email;

  /**
   * The login of the user. Must not be blank and must not contain spaces.
   */
  @NotBlank(message = "Login must not be blank.")
  @Pattern(regexp = "^[^\\s]+$", message = "Login should not contain spaces.")
  private String login;

  /**
   * The name of the user.
   */
  private String name;

  /**
   * The birthday of the user. Must be a date in the past.
   */
  @JsonFormat(pattern = "yyyy-MM-dd")
  @Past(message = "Birthday should be in the past.")
  private LocalDate birthday;

  private final Set<Long> friends = new HashSet<>();

}
