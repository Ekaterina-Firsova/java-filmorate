package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.List;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SearchCriteria;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.in_memory.InMemoryFilmStorage;

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
  List<Film> getTopFilms(int count, Long genreId, Integer year);

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

  /**
   * Retrieves a list of films directed by a specific director, sorted by the specified criteria.
   *
   * @param id     the ID of the director whose films are to be retrieved;
   * @param sortBy the sorting criteria; can be either "likes" to sort by the number of likes or
   *               "year" to sort by release year
   * @return a list of {@link Film} objects by the specified director, sorted according to
   * the {@code sortBy} parameter
   */
  List<Film> getDirectorFilms(Long id, String sortBy);

  /**
   * Retrieves a list of films recommended to a user based on their likes and friends' likes.
   *
   * @param userId the ID of the user.
   * @param similarUserId the ID of the user's friend.
   */
  Collection<Film> getRecommendedFilms(Long userId, Long similarUserId);

  /**
   * Retrieves a list of films that both a user and a friend have liked.
   *
   * @param userId the ID of the user.
   * @param friendId the ID of the user's friend.
   */
  Collection<Film> getCommonFilms(Long userId, Long friendId);


  /**
   * Searches for films based on the specified query and search criteria.
   *
   * @param query           the search query string used to filter films;
   * @param searchCriterias a list of {@link SearchCriteria} defining the criteria to search by;
   * @return a list of {@link Film} objects that match the search criteria
   */
  List<Film> searchBy(String query, List<SearchCriteria> searchCriterias);
}
