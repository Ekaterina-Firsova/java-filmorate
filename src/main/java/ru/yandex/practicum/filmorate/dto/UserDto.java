package ru.yandex.practicum.filmorate.dto;

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
import ru.yandex.practicum.filmorate.model.User;

/**
 * Data Transfer Object representing a User.
 * @see User
 */
@Data
@EqualsAndHashCode(of = {"email", "login", "birthday"})
@Builder
public class UserDto {

  private Long id;

  @NotBlank(message = "Email must not be blank.")
  @Email(message = "Email should be correct format")
  private String email;

  @NotBlank(message = "Login must not be blank.")
  @Pattern(regexp = "^[^\\s]+$", message = "Login should not contain spaces.")
  private String login;

  private String name;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @Past(message = "Birthday should be in the past.")
  private LocalDate birthday;

  private final Set<Long> friends = new HashSet<>();

}
