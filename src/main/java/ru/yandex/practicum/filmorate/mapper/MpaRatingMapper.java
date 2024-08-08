package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.model.MpaRating;

/**
 * Utility class for mapping between {@link MpaRating} and {@link MpaRatingDto}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MpaRatingMapper {

  public static MpaRatingDto mapToMpaRatingDto(final MpaRating mpa) {
    return MpaRatingDto.builder()
        .id(mpa.getId())
        .name(mpa.getName())
        .build();
  }
}
