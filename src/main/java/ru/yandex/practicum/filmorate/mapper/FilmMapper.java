package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

/**
 * Utility class for mapping between {@link Film} entity and {@link FilmDto}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {

  public static Film mapToFilm(final FilmDto filmDto) {
    final Film film = Film.builder()
        .id(filmDto.getId())
        .name(filmDto.getName())
        .description(filmDto.getDescription())
        .releaseDate(filmDto.getReleaseDate())
        .duration(filmDto.getDuration())
        .mpa(filmDto.getMpa())
        .build();
    filmDto.getGenres().forEach(genreId -> film.getGenres().add(genreId));
    filmDto.getLikes().forEach(userId -> film.getLikes().add(userId));
    return film;
  }

  public static FilmDto mapToFilmDto(final Film film) {
    final FilmDto filmDto = FilmDto.builder()
        .id(film.getId())
        .name(film.getName())
        .description(film.getDescription())
        .releaseDate(film.getReleaseDate())
        .duration(film.getDuration())
        .mpa(film.getMpa())
        .build();
    film.getGenres().forEach(genreId -> filmDto.getGenres().add(genreId));
    film.getLikes().forEach(userId -> filmDto.getLikes().add(userId));
    return filmDto;

  }
}
