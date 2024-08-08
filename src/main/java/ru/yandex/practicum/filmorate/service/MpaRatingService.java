package ru.yandex.practicum.filmorate.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaRatingMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaRatingService {

  private final MpaRatingStorage mpaRatingStorage;

  public List<MpaRatingDto> getAll() {
    log.debug("Inside getAll method.");
    return mpaRatingStorage.findAll().stream().map(MpaRatingMapper::mapToMpaRatingDto).toList();
  }

  public MpaRatingDto getById(Long id) {
    log.debug("Inside getByID method to get a mpa rating with ID = {}.", id);
    return MpaRatingMapper.mapToMpaRatingDto(getMpaRatingByIdOrThrow(id));
  }

  private MpaRating getMpaRatingByIdOrThrow(Long id) {
    log.debug("Getting a genre instance for ID = {} from the storage.", id);
    return mpaRatingStorage.findById(id)
        .orElseThrow(() -> new NotFoundException("Genre with ID = " + id + " doesn't exist."));
  }


}
