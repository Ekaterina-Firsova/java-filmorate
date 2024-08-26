package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ValidByValidatorTest {

  private Validator validator;

  @BeforeEach
  public void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @ParameterizedTest(name = "{0}")
  @DisplayName("Validation with valid 'by' value, No Constraint Violations")
  @MethodSource("provideValidByValue")
  public void testValidByValue(final String testName, final TestClass testClass) {
    final Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);

    Assertions.assertEquals(0, violations.size(),
        "Expected no constraint violations for valid value for the by field.");
  }

  @ParameterizedTest(name = "{0}")
  @DisplayName("Validation with invalid 'by' parameter, has Constraint Violations")
  @MethodSource("provideInvalidValidByValue")
  public void testInvalidByValue(final String testName, final TestClass testClass) {
    final Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
    final List<String> actualProperties = violations.stream()
        .map(v -> v.getPropertyPath().toString())
        .sorted().toList();

    Assertions.assertAll(
        () -> Assertions.assertNotEquals(0, violations.size(),
            "Expected constraint violations for invalid value(s) for the by field."),
        () -> Assertions.assertTrue(violations.stream()
                .allMatch(violation -> violation.getPropertyPath().toString().equals("by")),
            "The properties with violations do not match the expected properties.")
    );
  }

  private static Stream<Arguments> provideValidByValue() {
    final List<String> values = List.of(
        "title",
        "director",
        "genre",
        "title, director",
        "genre, title",
        "genre, title, director",
        "title, director, genre, title",
        "director, genre, title",
        ""
    );
    return Stream.of(
        Arguments.of("Valid by value = " + values.get(0), new TestClass(values.get(0))),
        Arguments.of("Valid by value = " + values.get(1), new TestClass(values.get(1))),
        Arguments.of("Valid by value = " + values.get(2), new TestClass(values.get(2))),
        Arguments.of("Valid by value = " + values.get(3), new TestClass(values.get(3))),
        Arguments.of("Valid by value = " + values.get(4), new TestClass(values.get(4))),
        Arguments.of("Valid by value = " + values.get(5), new TestClass(values.get(5))),
        Arguments.of("Valid by value = " + values.get(6), new TestClass(values.get(6))),
        Arguments.of("Multiple all valid + extra valid by value " + values.get(5),
            new TestClass(values.get(7))),
        Arguments.of("Valid by empty value", new TestClass(values.get(8))),
        Arguments.of("Valid by null value", new TestClass(null))
    );
  }

  private static Stream<Arguments> provideInvalidValidByValue() {
    final List<String> values = List.of(
        "title!",
        "friend",
        "actor, like, country",
        "title, like, country",
        "title, director, genre, actor"
    );
    return Stream.of(
        Arguments.of("Valid by value with invalid character", new TestClass(values.get(0))),
        Arguments.of("Invalid single by value = " + values.get(1), new TestClass(values.get(1))),
        Arguments.of("Multiple all invalid by values = " + values.get(2),
            new TestClass(values.get(2))),
        Arguments.of("Multiple only one valid value= " + values.get(3),
            new TestClass(values.get(3))),
        Arguments.of("Multiple all valid + extra invalid by value " + values.get(4),
            new TestClass(values.get(4)))
    );
  }

  /**
   * A simple inner class used for testing the {@link ValidBy} annotation. The 'by' parameter is
   * validated based on the provided options.
   */
  @Getter
  @AllArgsConstructor
  public static class TestClass {

    @ValidBy(byOptions = {"title", "director", "genre"})
    private final String by;

  }
}
