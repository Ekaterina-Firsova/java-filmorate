package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

/**
 * A service class that handles film related operations and interactions, including CRUD operations,
 * adding and removing likes, and retrieving top-rated films.
 * <p>
 * The service relies on the {@link FilmStorage} and {@link UserService} for data persistence and
 * user operations, respectively.
 * <ul>
 * <li>{@link #save(Film)}: Saves a new film to the storage.</li>
 * <li>{@link #update(Film)}: Updates an existing film in the storage.</li>
 * <li>{@link #getAll()}: Retrieves all films from the storage.</li>
 * <li>{@link #getById(Long)}: Retrieves a film by its ID.</li>
 * <li>{@link #getTopFilms(int)}: Retrieves the top-rated films based on the number of likes.</li>
 * <li>{@link #addLike(Long, Long)}: Adds a like to a film from a user.</li>
 * <li>{@link #removeLike(Long, Long)}: Removes a like from a film by a user.</li>
 * </ul>
 *
 * @see Film
 * @see FilmStorage
 * @see UserService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService implements CrudService<Film> {

  private final FilmStorage filmStorage;

  private final UserStorage userStorage;

  @Override
  public Film save(final Film film) {
    log.debug("Inside save Film method");
    return filmStorage.save(film);
  }

  @Override
  public Film update(final Film film) {
    log.debug("Inside update Film method");
    final Long id = film.getId();
    validateFilmId(id);
    return filmStorage.update(film);
  }

  @Override
  public Collection<Film> getAll() {
    log.debug("Inside getAll films method");
    return filmStorage.findAll();
  }

  @Override
  public Film getById(Long id) {
    log.debug("Inside getByID to get a film {}", id);
    return getFilmOrThrow(id);
  }

  public List<Film> getTopFilms(final int count) {
    log.debug("Inside the getTopFilms to get top {} films", count);
    return filmStorage.findAll().stream()
        .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
        .limit(count)
        .collect(Collectors.toList());
  }

  public Film addLike(final Long filmId, final Long userId) {
    log.debug("Inside the addLike method, user with ID {} likes the film with ID {} ", userId,
        filmId);
    final Film film = getFilmOrThrow(filmId);
    validateUserExist(userId);
    film.getLikes().add(userId);
    return film;
  }

  public Film removeLike(Long filmId, Long userId) {
    log.debug("Inside the removeLike method, user with ID [] ");
    final Film film = getFilmOrThrow(filmId);
    validateUserExist(userId);
    film.getLikes().remove(userId);
    return film;
  }

  private Film getFilmOrThrow(final Long id) {
    log.debug("Getting a film instance for ID {} from the {}}", id, filmStorage);
    return filmStorage.findById(id)
        .orElseThrow(() -> new NotFoundException("Film with Id = " + id + "not found."));
  }

  private void validateFilmId(final Long id) {
    log.debug("validating film ID {} is not null and exist", id);
    if (id == null || filmStorage.findById(id).isEmpty()) {
      throw new NotFoundException("Film with ID = " + id + " not found.");
    }
  }

  private void validateUserExist(final Long id) {
    if (!userStorage.isExist(id)) {
      throw new NotFoundException("User with Id = " + id + "not found.");
    }
  }

}
