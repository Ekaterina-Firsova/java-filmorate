package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Validator for the {@link DateAfter} annotation. Check if a date is after the specified date
 * constrains.
 */
public class DateAfterValidator implements ConstraintValidator<DateAfter, LocalDate> {

  private LocalDate afterDate;

  /**
   * Initializes the validator fields by parsing the date from 'after()' element's value of the
   * given annotation instance using specified format.
   *
   * @param constraintAnnotation the annotation instant with specified elements
   * @throws IllegalArgumentException if the specified date can not be parsed
   */
  @Override
  public void initialize(DateAfter constraintAnnotation) {
    String after = constraintAnnotation.after();
    String format = constraintAnnotation.format();
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
      this.afterDate = LocalDate.parse(after, formatter);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException(
          "Invalid date format for 'after' parameter. Use 'yyyy-MM-dd'.", e);
    }
  }

  /**
   * Provides validation of the provided date is  after the specified 'after' date.
   *
   * @param localDate                  the date to validate
   * @param constraintValidatorContext context in which the constraint is evaluated
   * @return {@code true} if the date is valid and is after the specified 'after' date,
   * {@code false} otherwise
   */
  @Override
  public boolean isValid(LocalDate localDate,
      ConstraintValidatorContext constraintValidatorContext) {
    if (localDate == null) {
      return true;
    }
    return !localDate.isBefore(afterDate);
  }
}
