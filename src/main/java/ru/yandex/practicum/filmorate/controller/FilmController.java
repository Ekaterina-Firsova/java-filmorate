package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

/**
 * Controller class for managing Films in the Filmorate application. All endpoints in this
 * controller are relative to the base path "/films".
 */
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

  private final Map<Long, Film> films = new HashMap<>();
  private Long idCount = 0L;

  /**
   * Handles POST requests to add a new film. Params:
   *
   * @param film the film to be added
   * @return the added film
   */
  @PostMapping
  public Film save(@Valid @RequestBody final Film film) {
    log.info("Received request to add new film: {}", film);
    final Film savedFilm = saveFilm(film);
    log.info("Film added successfully: {}", savedFilm);
    return savedFilm;
  }

  /**
   * Handles PUT requests to update an existing film.
   *
   * @param newFilm - the film with updated information
   * @return updated film
   */
  @PutMapping
  public Film update(@Valid @RequestBody final Film newFilm) {
    log.info("Received request to update film: {}", newFilm);
    final Film updatedFilm = updateFilm(newFilm);
    log.info("Film updated successfully: {}", updatedFilm);
    return updatedFilm;
  }

  /**
   * Handles GET requests to retrieve all films.
   *
   * @return a collection of all films
   */
  @GetMapping
  public Collection<Film> getAll() {
    return films.values();
  }

  /*  FilmService.java */

  /**
   * Saves a new film to the collection.
   *
   * @param film - the film to be saved
   * @return the saved film
   */
  Film saveFilm(final Film film) {
    log.debug("Entering saveFilm method.");
    checkDataDuplication(film);
    film.setId(getNextId());
    films.put(film.getId(), film);
    return film;
  }

  /**
   * Updates an existing film in the collection.
   *
   * @param film – the film with updated information
   * @return the  film information after the update
   */
  Film updateFilm(final Film film) {
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

  /**
   * Checks for data duplication in the collection of films.
   *
   * @param film – the film to check for duplication
   */
  private void checkDataDuplication(final Film film) {
    Optional<Film> duplicateFilm = films.values().stream()
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
    return ++idCount;
  }

}
