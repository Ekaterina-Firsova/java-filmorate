package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FilmValidationTest {

  static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

  private Validator validator;

  @BeforeEach
  public void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @ParameterizedTest(name = "{0}")
  @DisplayName("Film Object Validation with No Constraint Violations")
  @MethodSource("provideValidFilmInstances")
  public void testValidFilm(final String testName, final Film film) {
    final Set<ConstraintViolation<Film>> violations = validator.validate(film);
    Assertions.assertEquals(0, violations.size(),
        "Expected no constraint violations for valid Film object.");
  }

  @ParameterizedTest(name = "{0}")
  @DisplayName("Film Object Validation with Expected Constraint Violations")
  @MethodSource("provideInvalidFilmInstances")
  public void testInvalidFilm(String testName, final List<String> expectedProperties,
      final List<String> expectedMessages, final Film film) {

    Collections.sort(expectedProperties);
    Collections.sort(expectedMessages);

    final Set<ConstraintViolation<Film>> violations = validator.validate(film);
    final List<String> actualProperties = violations.stream()
        .map(v -> v.getPropertyPath().toString())
        .sorted().toList();
    final List<String> actualMessages = violations.stream().map(ConstraintViolation::getMessage)
        .sorted().toList();

    Assertions.assertEquals(expectedProperties.size(), violations.size(),
        "Number of violations does not match expected number of properties.");
    Assertions.assertIterableEquals(expectedProperties, actualProperties,
        "The properties with violations do not match the expected properties.");
    Assertions.assertIterableEquals(expectedMessages, actualMessages,
        "The violation messages do not match the expected messages.");
  }

  /**
   * Method provides data for test cases to validate valid Film Object with fields in conditions:
   * <ul>
   *   <li>
   *     All fields populated correctly;
   *   </li>
   *   <li>
   *     Only required fields are populated correctly - "name", "releaseDate".
   *   </li>
   * </ul>
   *
   * @return Stream of Arguments
   */
  private static Stream<Arguments> provideValidFilmInstances() {
    return Stream.of(
        Arguments.of("All fields populated correctly", Film.builder()
            .id(1L)
            .name("Movie")
            .description("Movie description")
            .releaseDate(LocalDate.now())
            .duration(150L)
            .build()),
        Arguments.of("Only required fields populated correctly", Film.builder()
            .name("Movie")
            .releaseDate(LocalDate.now())
            .build())
    );
  }

  /**
   * Method provides data for cases to validate an incorrect Film object with fields in conditions:
   * <ul>
   *   <li>
   *     "name" -> is null, empty String, String with spaces;
   *   </li>
   *   <li>
   *     "description" -> contain more than 200 characters;
   *   </li>
   *   <li>
   *     "releaseDate" -> null, date in the future, date before MIN_DATE;
   *   </li>
   *   <li>
   *     "duration" -> zero, negative number.
   *   </li>
   * </ul>
   *
   * @return Stream of Arguments
   */
  private static Stream<Arguments> provideInvalidFilmInstances() {
    final List<String> testNames = List.of(
        "All fields are null",
        "Name is empty, ReleaseDate - in future, Duration - zero",
        "Name is Blank, Description - 201 characters, ReleaseDate - before MinDate, Duration - negative number");
    final List<List<String>> expectedProperties = List.of(
        new ArrayList<>(Arrays.asList("name", "releaseDate")),
        new ArrayList<>(Arrays.asList("duration", "name", "releaseDate")),
        new ArrayList<>(Arrays.asList("description", "duration", "name", "releaseDate"))
    );
    final List<List<String>> expectedMessages = List.of(
        new ArrayList<>(
            Arrays.asList("Name should not be empty.", "ReleaseDate should not be null.")),
        new ArrayList<>(
            Arrays.asList("Name should not be empty.", "Release date should not be in future.",
                "Duration must be a positive number.")),
        new ArrayList<>(Arrays.asList("Name should not be empty.",
            "Description should not exceed 200 characters.",
            "Release date should not be before 1895-12-28",
            "Duration must be a positive number.")));
    final List<Film> filmsToValidate = List.of(
        Film.builder().build(),
        Film.builder().name("")
            .releaseDate(LocalDate.now().plusDays(1)).duration(0L).build(),
        Film.builder().name("  ").description(generateStringOfLength(201))
            .releaseDate(MIN_DATE.minusDays(1)).duration(-1L).build());

    return Stream.of(
        Arguments.of(testNames.get(0),
            expectedProperties.get(0),
            expectedMessages.get(0),
            filmsToValidate.get(0)),
        Arguments.of(testNames.get(1),
            expectedProperties.get(1),
            expectedMessages.get(1),
            filmsToValidate.get(1)),
        Arguments.of(testNames.get(2),
            expectedProperties.get(2),
            expectedMessages.get(2),
            filmsToValidate.get(2)));
  }

  private static String generateStringOfLength(final int count) {
    return "a".repeat(count);
  }
}