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
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

/**
 * Controller class responsible for handling requests related to the mpa rating  for films in the
 * Filmorate application. Provides endpoints to retrieve mpa rating information for the film.
 * <p> All endpoints in this controller are relative to the base path {@code "/mpa"}.
 */
@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
@Validated
public class MpaRatingController {

  private final MpaRatingService mpaRatingService;

  /**
   * Retrieves a list of all available mpa rates. Handles GET request to the base path.
   *
   * @return a list of {@link MpaRatingDto} representing all mpa rating available.
   */
  @GetMapping
  public List<MpaRatingDto> getAllMpaRatings() {
    log.info("Received GET /mpa.");
    return mpaRatingService.getAll();
  }

  /**
   * Retrieves details of a specific mpa its ID. Handles GET request to the path  "/mpa/{id}".
   *
   * @param id - The ID of the mpa to retrieve.
   * @return a {@link MpaRatingDto} representing the mpa details.
   */
  @GetMapping("/{id}")
  public MpaRatingDto getGenre(@PathVariable("id") @NotNull final Long id) {
    log.info("Received GET /mpa/{}.", id);
    return mpaRatingService.getById(id);
  }

}
