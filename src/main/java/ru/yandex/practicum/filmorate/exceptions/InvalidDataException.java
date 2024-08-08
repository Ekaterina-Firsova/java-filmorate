package ru.yandex.practicum.filmorate.exceptions;

public class InvalidDataException extends RuntimeException {

  public InvalidDataException(final String message) {
    super(message);
  }
}
