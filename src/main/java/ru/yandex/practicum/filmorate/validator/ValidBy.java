package ru.yandex.practicum.filmorate.validator;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The custom Annotation to validate that a parameter by for the search method has valid value.
 */
@Documented
@Constraint(validatedBy = ValidByValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBy {

  /**
   * The default error message is  used when validation fails.
   *
   * @return the error message template
   */
  String message() default "Invalid parameter value. Should be in a list: {byOptions}.";

  /**
   * The valid values for the by parameter.
   *
   * @return the by options
   */
  String[] byOptions() default {};

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
