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
public class UserValidationTest {

  private Validator validator;

  @BeforeEach
  public void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @ParameterizedTest(name = "{0}")
  @DisplayName("User Object Validation with No Constraint Violations")
  @MethodSource("provideValidUserInstances")
  public void testValidUser(final String testName, final User user) {
    final Set<ConstraintViolation<User>> violations = validator.validate(user);
    Assertions.assertEquals(0, violations.size(),
        "Expected no constraint violations for valid User object.");
  }

  @ParameterizedTest(name = "{0}")
  @DisplayName("User Object Validation with Expected Constraint Violations")
  @MethodSource("provideInvalidUserInstances")
  public void testInvalidUser(String testName, final List<String> expectedProperties,
      final List<String> expectedMessages, final User user) {

    Collections.sort(expectedProperties);
    Collections.sort(expectedMessages);

    final Set<ConstraintViolation<User>> violations = validator.validate(user);
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
   * Method provides data for test cases to validate valid User Object with fields in conditions:
   * <ul>
   *   <li>
   *     All fields populated correctly;
   *   </li>
   *   <li>
   *     Only required fields are populated correctly - "email", "login".
   *   </li>
   * </ul>
   *
   * @return Stream of Arguments
   */
  private static Stream<Arguments> provideValidUserInstances() {
    return Stream.of(
        Arguments.of("All fields populated correctly", User.builder()
            .id(1L)
            .email("validEmail@test.ru")
            .login("GoodLogin")
            .name("Name Always is Valid")
            .birthday(LocalDate.now().minusDays(1))
            .build()),
        Arguments.of("Only required fields populated correctly", User.builder()
            .email("validEmail@test.ru")
            .login("GoodLogin")
            .build())
    );
  }

  /**
   * Method provides data for cases to validate an incorrect User object with fields in conditions:
   * <ul>
   *   <li>
   *     "email" -> is null, empty, one space, incorrect format;
   *   </li>
   *   <li>
   *     "login" -> null, empty, one space, is a String of characters with space;
   *   </li>
   *   <li>
   *     "birthday" -> in a future, today.
   *   </li>
   * </ul>
   *
   * @return Stream of Arguments
   */
  private static Stream<Arguments> provideInvalidUserInstances() {
    final List<String> testNames = List.of(
        "All fields are null",
        "Email and Login are empty, Birthday - in a future",
        "Email and Login are blank, Birthday - today",
        "Email - incorrect format, Login - String with spaces");
    final List<List<String>> expectedProperties = List.of(
        new ArrayList<>(Arrays.asList("email", "login")),
        new ArrayList<>(Arrays.asList("email", "login", "login", "birthday")),
        new ArrayList<>(Arrays.asList("email", "email", "login", "login", "birthday")),
        new ArrayList<>(Arrays.asList("email", "login")));
    final List<List<String>> expectedMessages = List.of(
        new ArrayList<>(
            Arrays.asList("Email must not be blank.", "Login must not be blank.")),
        new ArrayList<>(
            Arrays.asList("Email must not be blank.", "Login must not be blank.",
                "Login should not contain spaces.", "Birthday should be in the past.")),
        new ArrayList<>(
            Arrays.asList("Email must not be blank.", "Email should be correct format",
                "Login must not be blank.",
                "Login should not contain spaces.", "Birthday should be in the past.")),
        new ArrayList<>(
            Arrays.asList("Email should be correct format", "Login should not contain spaces.")));
    final List<User> usersToValidate = List.of(
        User.builder().build(),
        User.builder().email("").login("").birthday(LocalDate.now().plusDays(1)).build(),
        User.builder().email(" ").login(" ").birthday(LocalDate.now()).build(),
        User.builder().email("это-неправильный?email@").login("Login with spaces").build());

    return Stream.of(
        Arguments.of(testNames.get(0),
            expectedProperties.get(0),
            expectedMessages.get(0),
            usersToValidate.get(0)),
        Arguments.of(testNames.get(1),
            expectedProperties.get(1),
            expectedMessages.get(1),
            usersToValidate.get(1)),
        Arguments.of(testNames.get(2),
            expectedProperties.get(2),
            expectedMessages.get(2),
            usersToValidate.get(2)),
        Arguments.of(testNames.get(3),
            expectedProperties.get(3),
            expectedMessages.get(3),
            usersToValidate.get(3)));
  }

}
