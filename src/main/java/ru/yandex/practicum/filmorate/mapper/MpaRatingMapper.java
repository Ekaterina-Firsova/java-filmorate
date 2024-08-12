package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.model.MpaRating;

/**
 * Utility class for mapping between {@link MpaRating} and {@link MpaRatingDto}.
 */
@UtilityClass
public class MpaRatingMapper {

  public MpaRatingDto mapToMpaRatingDto(final MpaRating mpa) {
    return MpaRatingDto.builder()
        .id(mpa.getId())
        .name(mpa.getName())
        .build();
  }
}
