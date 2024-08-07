package ru.yandex.practicum.filmorate.storage.inMemory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

  private final Map<Long, Film> films = new HashMap<>();
  private Long lastId = 0L;


  @Override
  public Film save(final Film film) {
    checkDataDuplication(film);
    film.setId(getNextId());
    films.put(film.getId(), film);
    return film;
  }

  @Override
  public Optional<Film> findById(final Long id) {
    return Optional.ofNullable(films.get(id));
  }

  @Override
  public Collection<Film> findAll() {
    return films.values();
  }

  @Override
  public Film update(final Film film) {
    checkDataDuplication(film);
    films.put(film.getId(), film);
    return film;
  }

  @Override
  public void delete(final Long id) {
    films.remove(id);
  }

  /**
   * Checks for data duplication in the collection of films. Verifies whether the given film already
   * exists in the collection of films, excluding itself if it is already present (based on ID
   * comparison). If a duplicate is found (a film with the same data but different ID), a
   * {@link DuplicatedDataException} is thrown.
   *
   * @param film the film to check for duplication
   * @throws DuplicatedDataException if the action would result in duplication
   */
  private void checkDataDuplication(final Film film) {
    boolean isDuplicate = films.values().stream()
        .anyMatch(existingFilm ->
            !existingFilm.getId().equals(film.getId()) && existingFilm.equals(film));
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
