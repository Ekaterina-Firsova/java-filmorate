package ru.yandex.practicum.filmorate.validator;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The custom Annotation to validate that a date field is after a specified date.
 */
@Documented
@Constraint(validatedBy = DateAfterValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateAfter {

  /**
   * The default error message is  used when validation fails.
   *
   * @return the error message template
   */
  String message() default "Invalid date.Release date must be after {after}.";

  /**
   * The date to compare against.
   *
   * @return the date
   */
  String after();

  /**
   * The date format that should be e used for parsing the 'after' date.
   *
   * @return the date format
   */
  String format() default "yyyy-MM-dd";

  /**
   * Groups for categorizing constraints.
   *
   * @return the groups
   */
  Class<?>[] groups() default {};

  /**
   * Payload for providing custom details about the validation failure.
   *
   * @return the payload
   */
  Class<? extends Payload>[] payload() default {};

}
