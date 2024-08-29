package ru.yandex.practicum.filmorate.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.InvalidDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.SearchCriteria;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

/**
 * Service implementation for managing film related operations.
 * <p>
 * This service uses {@link FilmStorage} for data persistence, {@link UserService} for user-related
 * operations, {@link GenreStorage} and {@link MpaRatingStorage} for genre and MPA rating
 * management, and {@link EventService} for event saving. It also interacts with
 * {@link DirectorService} for director-related operations.
 * <p>
 *
 * @see FilmService
 * @see FilmDto
 * @see FilmStorage
 * @see UserService
 * @see GenreStorage
 * @see MpaRatingStorage
 * @see EventService
 * @see DirectorService
 */
@Service
@Slf4j
public class FilmServiceImpl implements FilmService {

  private final FilmStorage filmStorage;
  private final UserService userService;
  private final GenreStorage genreStorage;
  private final MpaRatingStorage mpaStorage;
  private final EventService eventService;
  private final DirectorService directorService;

  public FilmServiceImpl(@Qualifier("filmDbStorage") final FilmStorage filmStorage,
      final UserService userService,
      final GenreStorage genreStorage,
      final MpaRatingStorage mpaStorage,
      final EventService eventService,
      final DirectorService directorService) {
    this.filmStorage = filmStorage;
    this.userService = userService;
    this.genreStorage = genreStorage;
    this.mpaStorage = mpaStorage;
    this.eventService = eventService;
    this.directorService = directorService;
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
    return filmStorage.findAll().stream()
        .map(FilmMapper::mapToFilmDto)
        .toList();
  }

  @Override
  public FilmDto getById(final Long id) {
    log.debug("Inside getByID to get a film {}", id);
    return FilmMapper.mapToFilmDto(getFilmOrThrow(id));
  }

  @Override
  public void removeById(Long id) {
    log.debug("Deleting film with ID {} ", id);
    filmStorage.delete(id);
  }


  @Override
  public FilmDto addLike(final Long filmId, final Long userId) {
    log.debug("Inside the addLike method, user with ID {} likes the film with ID {} ", userId,
        filmId);
    validateFilmId(filmId);
    validateUserExist(userId);

    final FilmDto likedFilm = FilmMapper.mapToFilmDto(filmStorage.addLike(filmId, userId));
    log.debug("User with id {} added like for the film with id {} successfully", userId,
        filmId);

    eventService.addEvent(userId, filmId, EventType.LIKE, Operation.ADD);

    return likedFilm;
  }

  @Override
  public FilmDto removeLike(final Long filmId, final Long userId) {
    validateFilmId(filmId);
    validateUserExist(userId);
    final FilmDto unlikedFilm = FilmMapper.mapToFilmDto(filmStorage.removeLike(filmId, userId));
    log.debug("User with id {} removed like from the film with id {} successfully", userId,
        filmId);

    eventService.addEvent(userId, filmId, EventType.LIKE, Operation.REMOVE);

    return unlikedFilm;
  }

  @Override
  public List<FilmDto> getTopFilms(final int count, final Long genreId, final Integer year) {
    log.debug("Inside the getTopFilms to get top {} films", count);
    return filmStorage.getTopFilms(count, genreId, year)
        .stream()
        .map(FilmMapper::mapToFilmDto)
        .toList();
  }

  @Override
  public List<FilmDto> getDirectorFilms(final Long id, final String sortBy) {
    directorService.validateDirectorId(id);
    return filmStorage.getDirectorFilms(id, sortBy)
        .stream()
        .map(FilmMapper::mapToFilmDto)
        .toList();
  }

  @Override
  public Collection<Film> getCommonFilms(Long userId, Long friendId) {
    return filmStorage.getCommonFilms(userId, friendId);
  }

  @Override
  public List<FilmDto> search(final String query, final String by) {
    log.debug("Inside search method for query {} by parameters by {}.", query, by);

    final List<SearchCriteria> searchCriteria = Arrays.stream(by.split(","))
        .map(String::trim)
        .map(SearchCriteria::fromString)
        .toList();
    return filmStorage.searchBy(query, searchCriteria)
        .stream()
        .map(FilmMapper::mapToFilmDto)
        .toList();
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
    userService.validateUserId(id);
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
