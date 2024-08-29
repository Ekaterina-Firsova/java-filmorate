package ru.yandex.practicum.filmorate.exception;

public class DuplicatedDataException extends RuntimeException {

  public DuplicatedDataException(final String message) {
    super(message);
  }
}
