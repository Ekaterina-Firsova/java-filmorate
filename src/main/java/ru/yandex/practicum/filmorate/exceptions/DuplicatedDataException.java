package ru.yandex.practicum.filmorate.exceptions;

public class DuplicatedDataException extends RuntimeException {

  public DuplicatedDataException(final String message) {
    super(message);
  }
}
