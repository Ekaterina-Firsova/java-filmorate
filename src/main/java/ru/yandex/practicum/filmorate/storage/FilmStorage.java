package ru.yandex.practicum.filmorate.storage;

import java.util.List;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.inMemory.InMemoryFilmStorage;

/**
 * Interface for managing {@link Film} entities in the storage system. Extends the {@link Storage}
 * interface to include film-specific operations.
 *
 * @see Storage
 * @see InMemoryFilmStorage
 * @see FilmDbStorage
 */
public interface FilmStorage extends Storage<Film> {

  /**
   * Retrieves a list of the top-rated films based on the number of likes.
   *
   * @param count the maximum number of top films to retrieve.
   * @return a list of {@link Film} representing the top-rated films.
   */
  List<Film> getTopFilms(int count);

  /**
   * Adds a like from a user to a specified film.
   *
   * @param filmId the ID of the film to which the like is being added.
   * @param userId the ID of the user who is liking the film.
   * @return the updated {@link Film} after the like has been added.
   * @throws NotFoundException if the film or user does not exist.
   */
  Film addLike(Long filmId, Long userId);

  /**
   * Removes a like from a user from a specified film.
   *
   * @param filmId the ID of the film from which the like is being removed.
   * @param userId the ID of the user who is removing the like.
   * @return the updated {@link Film} after the like has been removed.
   * @throws NotFoundException if the film or user does not exist.
   */
  Film removeLike(Long filmId, Long userId);

  List<Film> getDirectorFilms(Long id, String sortBy);
}
