package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

/**
 * Utility class for mapping between {@link Genre} and {@link GenreDto}.
 */
@UtilityClass
public class GenreMapper {

  public GenreDto mapToGenreDto(final Genre genre) {
    return GenreDto.builder()
        .id(genre.getId())
        .name(genre.getName())
        .build();
  }

}
