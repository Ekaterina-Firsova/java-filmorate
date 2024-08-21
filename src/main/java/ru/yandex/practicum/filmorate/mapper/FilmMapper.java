package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

/**
 * Utility class for mapping between {@link Film} entity and {@link FilmDto}.
 */
@UtilityClass
public class FilmMapper {

  public Film mapToFilm(final FilmDto filmDto) {
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
    filmDto.getDirectors().forEach(director -> film.getDirectors().add(director));
//    filmDto.getDirector().addAll(film.getDirector());
    return film;
  }

  public FilmDto mapToFilmDto(final Film film) {
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
    film.getDirectors().forEach(director -> filmDto.getDirectors().add(director));
//    film.getDirector().addAll(filmDto.getDirector());
    return filmDto;

  }
}
