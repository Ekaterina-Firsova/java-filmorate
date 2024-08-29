package ru.yandex.practicum.filmorate.exception;

public class InvalidDataException extends RuntimeException {

  public InvalidDataException(final String message) {
    super(message);
  }
}
