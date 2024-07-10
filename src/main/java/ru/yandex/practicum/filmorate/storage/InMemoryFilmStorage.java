package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

  private final Map<Long, Film> films = new HashMap<>();
  private Long lastId = 0l;


  @Override
  public Film save(final Film film) {
    log.debug("Entering saveFilm method.");
    checkDataDuplication(film); //throws DuplicatedDataException if found
    film.setId(getNextId());
    films.put(film.getId(), film);
    return film;
  }

  @Override
  public Optional<Film> findById(Long id) {
    return Optional.empty();
  }

  @Override
  public Collection<Film> findAll() {
    return films.values();
  }

  @Override
  public Film update(final Film film) {
    log.debug("Entering updateFilm method.");
    final Long id = film.getId();
    if (id == null) {
      throw new IllegalArgumentException("Film ID must be provided.");
    }
    final Film oldFilm = films.get(id);
    log.debug("Film before updating: {}", oldFilm);
    if (oldFilm == null) {
      throw new NotFoundException("Film with ID " + id + " not found.");
    }
    checkDataDuplication(film);
    films.put(id, film);
    log.debug("Film after updating: {}", film);
    return film;
  }

  @Override
  public void delete(Long id) {

  }

  /**
   * Checks for data duplication in the collection of films.
   *
   * @param film – the film to check for duplication
   */
  private void checkDataDuplication(final Film film) {
    final Optional<Film> duplicateFilm = films.values().stream()
        .filter(f -> f.equals(film))
        .findFirst();
    boolean isDuplicate = duplicateFilm.isPresent();
    log.debug("Checking duplication - {}", isDuplicate);
    if (isDuplicate) {
      throw new DuplicatedDataException("Action would result in duplication.");
    }
  }
  /**
   * Generates the next available film ID.
   *
   * @return the next available film ID
   */
  private long getNextId() {
    return ++lastId;
  }
}
