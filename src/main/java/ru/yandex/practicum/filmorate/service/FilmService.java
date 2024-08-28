package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exceptions.InvalidDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

/**
 * A service class that handles film related operations and interactions, including CRUD operations,
 * adding and removing likes, and retrieving top-rated films.
 * <p>
 * The service relies on the {@link FilmStorage} and {@link UserService} for data persistence and
 * user operations, respectively.
 * <ul>
 * <li>{@link #save(FilmDto)}: Saves a new film to the storage.</li>
 * <li>{@link #update(FilmDto)}: Updates an existing film in the storage.</li>
 * <li>{@link #getAll()}: Retrieves all films from the storage.</li>
 * <li>{@link #getById(Long)}: Retrieves a film by its ID.</li>
 * <li>{@link #getTopFilms(int)}: Retrieves the top-rated films based on the number of likes.</li>
 * <li>{@link #addLike(Long, Long)}: Adds a like to a film from a user.</li>
 * <li>{@link #removeLike(Long, Long)}: Removes a like from a film by a user.</li>
 * </ul>
 *
 * @see Film
 * @see FilmDto
 * @see FilmStorage
 * @see UserStorage
 */
@Service
@Slf4j
public class FilmService implements CrudService<FilmDto> {

  private final FilmStorage filmStorage;
  private final UserStorage userStorage;
  private final GenreStorage genreStorage;
  private final MpaRatingStorage mpaStorage;


  @Autowired
  public FilmService(@Qualifier("filmDbStorage") final FilmStorage filmStorage,
      @Qualifier("userDbStorage") final UserStorage userStorage,
      final GenreStorage genreStorage,
      final MpaRatingStorage mpaStorage) {
    this.filmStorage = filmStorage;
    this.userStorage = userStorage;
    this.genreStorage = genreStorage;
    this.mpaStorage = mpaStorage;
  }

  @Override
  public FilmDto save(final FilmDto film) {
    log.debug("Inside save Film method");
    validateMpa(film.getMpa());
    validateGenres(film.getGenres());

    return FilmMapper.mapToFilmDto(filmStorage.save(FilmMapper.mapToFilm(film)));
  }

  @Override
  public FilmDto update(final FilmDto film) {
    log.debug("Inside update Film method");
    final Long id = film.getId();
    validateFilmId(id);
    validateMpa(film.getMpa());
    validateGenres(film.getGenres());

    return FilmMapper.mapToFilmDto(filmStorage.update(FilmMapper.mapToFilm(film)));
  }

  @Override
  public Collection<FilmDto> getAll() {
    log.debug("Inside getAll films method");
    return filmStorage.findAll().stream().map(FilmMapper::mapToFilmDto).toList();
  }

  @Override
  public FilmDto getById(final Long id) {
    log.debug("Inside getByID to get a film {}", id);
    return FilmMapper.mapToFilmDto(getFilmOrThrow(id));
  }

  public List<FilmDto> getTopFilms(final int count) {
    log.debug("Inside the getTopFilms to get top {} films", count);
    return filmStorage.getTopFilms(count).stream().map(FilmMapper::mapToFilmDto).toList();

  }

  public FilmDto addLike(final Long filmId, final Long userId) {
    log.debug("Inside the addLike method, user with ID {} likes the film with ID {} ", userId,
        filmId);
    validateFilmId(filmId);
    validateUserExist(userId);
    return FilmMapper.mapToFilmDto(filmStorage.addLike(filmId, userId));
  }

  public FilmDto removeLike(final Long filmId, final Long userId) {
    log.debug("Inside the removeLike method, user with ID [] ");
    validateFilmId(filmId);
    validateUserExist(userId);
    return FilmMapper.mapToFilmDto(filmStorage.removeLike(filmId, userId));
  }

  private Film getFilmOrThrow(final Long id) {
    log.debug("Getting a film instance for ID {} from the {}}", id, filmStorage);
    return filmStorage.findById(id)
        .orElseThrow(() -> new NotFoundException("Film with Id = " + id + "not found."));
  }

  private void validateFilmId(final Long id) {
    log.debug("validating film ID {} is not null and exist", id);
    if (id == null || !filmStorage.isExist(id)) {
      log.warn("Film with ID = {} not found.", id);
      throw new NotFoundException("Film with ID = " + id + " not found.");
    }
  }

  private void validateUserExist(final Long id) {
    if (!userStorage.isExist(id)) {
      log.warn("User with ID {} not found.", id);
      throw new NotFoundException("User with Id = " + id + "not found.");
    }
  }

  private void validateGenres(Set<Genre> genres) {
    log.debug("Validating film  genres is existed in db.");
    if (genres.isEmpty()) {
      return;
    }
    final Set<Long> genreIds = genres.stream().map(Genre::getId).collect(Collectors.toSet());
    final Integer existONes = genreStorage.countExistedIds(genreIds);

    if (genreIds.size() != existONes) {
      log.warn("Validating genres failed: ids for validating");
      throw new InvalidDataException("One or more genres do not exist.");
    }
  }

  private void validateMpa(final MpaRating mpa) {
    log.debug("Validating MPA rate is existed.");
    if (!mpaStorage.isExist(mpa.getId())) {
      log.warn("Validating MPA rate failed: {} does not exist in db.", mpa);
      throw new InvalidDataException("MPA rate with ID = " + mpa.getId() + "not found.");
    }
  }

}
