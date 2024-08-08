package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * Represents the Motion Picture Association (MPA) ratings in Filmorate application.
 * <p> The MPA rating is a classification assigned to films indicating the appropriate age group
 * for viewers. Each film can have one MPA rating, which provides guidance on the content
 * suitability for different audiences. Examples of MPA ratings include "G", "PG", "PG-13", "R", and
 * "NC-17".
 */
@Data
@Builder
public class MpaRating {

  private static final int MAX_NAME_LENGTH = 100;

  private Long id;

  @NotBlank(message = "Name should not be empty.")
  @Size(max = MAX_NAME_LENGTH, message = "Mpa rating name should not exceed "
      + MAX_NAME_LENGTH + " characters.")
  private String name;

}
