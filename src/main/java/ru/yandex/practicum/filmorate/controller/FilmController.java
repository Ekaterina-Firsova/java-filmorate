package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

/**
 * Controller class for managing Films in the Filmorate application. All endpoints in this
 * controller are relative to the base path "/films".
 */
@Slf4j
@RestController
@RequestMapping("/films")
@Validated
@RequiredArgsConstructor
public class FilmController {

  private final FilmService filmService;

  /**
   * Handles POST requests to add a new film. Params:
   *
   * @param film the film to be added
   * @return the added film
   */
  @PostMapping
  public Film save(@Valid @RequestBody final Film film) {
    log.info("Received request POST /films with body : {}", film);
    final Film savedFilm = filmService.save(film);
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
    log.info("Received request PUT /films with body: {}.", newFilm);
    final Film updatedFilm = filmService.update(newFilm);
    log.info("Film updated successfully: {}.", updatedFilm);
    return updatedFilm;
  }

  /**
   * Handles PUT request to add Like reaction to the specified film ID from the user .
   *
   * @param id     The ID of film to add a like reaction. Must not be null.
   * @param userId The ID of user, who mark films with like reaction. Must not be null.
   * @return the updated film with a like added.
   */
  @PutMapping("/{id}/like/{userId}")
  public Film addLike(@PathVariable("id") @NotNull final Long id,
      @PathVariable("userId") @NotNull final Long userId) {
    log.info("Received request PUT films/{}/like/{}.", id, userId);
    final Film filmWithNewLike = filmService.addLike(id, userId);
    log.info("Like was added to the film successfully: {}.", filmWithNewLike);
    return filmWithNewLike;
  }

  /**
   * Handles GET requests to retrieve all films.
   *
   * @return a collection of all films
   */
  @GetMapping
  public Collection<Film> getAll() {
    log.info("Received request GET /films.");
    return filmService.getAll();
  }


  /**
   * Handles GET request to retrieve the most popular films based on the number of likes.
   *
   * @param count The number of top films to return. Must be positive integer. If not specified,
   *              defaults to 10.
   * @return A list of the most popular films, limited by the specified count.
   */
  @GetMapping("/popular")
  public List<Film> getTopByLikes(@RequestParam(defaultValue = "10") @Min(1) final Integer count) {
    log.info("Received request GET /films/popular?count={}", count);
    final List<Film> mostPopularFilms = filmService.getTopFilms(count);
    log.info("Returning top {} films : {}", count, mostPopularFilms);
    return mostPopularFilms;
  }

  /**
   * Handles DELETE request to remove like from the specified user for the film
   *
   * @param id     The film ID to remove like from.
   * @param userId The user ID, who intend to remove like.
   * @return Updated film with the like removed.
   */
  @DeleteMapping("/{id}/like/{userId}")
  public Film deleteLike(@PathVariable("id") @NotNull Long id,
      @PathVariable("userId") @NotNull Long userId) {
    log.info("Received request DELETE /films/{}/like/{}", id, userId);
    final Film filmWithoutLike = filmService.removeLike(id, userId);
    log.info("Like was removed successfully from the film {}", filmWithoutLike);
    return filmWithoutLike;
  }
}
