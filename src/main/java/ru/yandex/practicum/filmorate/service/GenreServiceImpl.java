package ru.yandex.practicum.filmorate.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreServiceImpl implements GenreService {

  private final GenreStorage genreStorage;

  @Override
  public List<GenreDto> getAll() {
    log.debug("Inside getAll method.");
    return genreStorage.findAll()
        .stream()
        .map(GenreMapper::mapToGenreDto)
        .toList();
  }

  @Override
  public GenreDto getById(Long id) {
    log.debug("Inside getByID method to get a genre with ID = {}.", id);
    return GenreMapper.mapToGenreDto(getGenreByIdOrThrow(id));
  }

  private Genre getGenreByIdOrThrow(Long id) {
    log.debug("Getting a genre instance for ID = {} from the storage.", id);
    return genreStorage.findById(id)
        .orElseThrow(() -> new NotFoundException("Genre with ID = " + id + " doesn't exist."));
  }
}
