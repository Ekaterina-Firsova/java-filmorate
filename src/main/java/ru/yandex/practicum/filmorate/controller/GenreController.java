package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.service.GenreService;

/**
 * Controller class responsible for handling requests related to genres in the Filmorate Application.
 * Provides endpoints to retrieve genre information which can be associated with films.
 * All endpoints in this controller are relative to the base path {@code "/genres'}.
 */
@Slf4j
@RestController
@RequestMapping("/genres")
@Validated
@RequiredArgsConstructor
public class GenreController {

  private final GenreService genreService;

  /**
   * Retrieves a list of all available genres.
   * Handles GET request to the base path.
   * @return a list of {@link GenreDto} representing all genres.
   */
  @GetMapping
  public List<GenreDto> getAllGenres() {
    log.info("Received GET /genres.");
    return genreService.getAll();
  }

  /**
   * Retrieves details of a specific genre by its ID.
   * Handles GET request to the path  "/genres/{id}".
   * @param id - The ID of the genre to retrieve.
   * @return a {@link GenreDto} representing the genre details.
   */
  @GetMapping("/{id}")
  public GenreDto getGenre(@PathVariable("id") @NotNull final Long id) {
    log.info("Received GET /genres/{}.", id);
    return genreService.getById(id);
  }
}
