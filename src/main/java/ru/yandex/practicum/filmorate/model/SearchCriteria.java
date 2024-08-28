package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

/**
 * Enum representing search criteria for film searches.
 * Each enum constant maps to a specific column in the film database.
 */
@Getter
public enum SearchCriteria {
  /**
   * Search by film title.
   */
  TITLE("f.NAME"),
  /**
   * Search by film director.
   */
  DIRECTOR("d.NAME");

  private final String tableColumn;

  SearchCriteria(String tableColumn) {
    this.tableColumn = tableColumn;
  }

  /**
   * Converts a string to a corresponding {@link SearchCriteria} enum constant.
   *
   * @param criteria the string value of the search criteria
   * @return the {@link SearchCriteria} enum constant that matches the given string
   * @throws IllegalArgumentException if the provided string does not match any of the enum constants
   */
  public static SearchCriteria fromString(String criteria) {
    for (SearchCriteria sc : SearchCriteria.values()) {
      if (sc.name().equalsIgnoreCase(criteria)) {
        return sc;
      }
    }
    throw new IllegalArgumentException("Invalid search criteria: " + criteria);
  }
}
