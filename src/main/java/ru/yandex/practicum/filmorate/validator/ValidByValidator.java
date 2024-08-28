package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

/**
 * Validator for the {@link ValidBy} annotation. Check if a by parameter has correct value  that has
 * been specified in constrains.
 */
public class ValidByValidator implements ConstraintValidator<ValidBy, String> {

  private List<String> validByOptions;

  /**
   * Initializes the validator with the valid options provided in the {@link ValidBy} annotation.
   *
   * @param constraintAnnotation the annotation instant with specified elements
   * @throws IllegalArgumentException if the specified element is invalid
   */
  @Override
  public void initialize(ValidBy constraintAnnotation) {
    validByOptions = Arrays.asList(constraintAnnotation.byOptions());
  }

  /**
   * Provides validation of the 'by' parameter to ensure it contains only valid values as specified
   * in the {@link ValidBy}annotation.
   *
   * @param by                         parameter to validate
   * @param constraintValidatorContext context in which the constraint is evaluated
   * @return {@code true} if all values are valid, {@code false} otherwise
   */
  @Override
  public boolean isValid(String by, ConstraintValidatorContext constraintValidatorContext) {
    if (by == null || by.isEmpty()) {
      return true;
    }
    return Arrays.stream(by.split(","))
        .map(String::trim)
        .allMatch(b -> validByOptions.contains(b));
  }

}
