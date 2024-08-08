package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

/**
 * Utility class for mapping between {@link Genre} and {@link GenreDto}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenreMapper {

  public static GenreDto mapToGenreDto(final Genre genre) {
    return GenreDto.builder()
        .id(genre.getId())
        .name(genre.getName())
        .build();
  }

}
