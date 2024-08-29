package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

@UtilityClass
public class DirectorMapper {

  public DirectorDto mapToDirectorDto(final Director director) {
    return DirectorDto.builder()
        .id(director.getId())
        .name(director.getName())
        .build();
  }

  public Director mapToDirector(final DirectorDto directorDto) {
    return directorDto != null ?
        Director.builder()
            .id(directorDto.getId())
            .name(directorDto.getName())
            .build() :
        null;
  }
}
