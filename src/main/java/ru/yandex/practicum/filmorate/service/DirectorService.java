package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDbStorage;

/**
 * A service class that handles operations related to directors.
 * <p>
 * The service relies on an underlying storage mechanism provided by {@link Storage} and manages
 * director-related actions such as saving, updating, and retrieving directors.
 * <p>
 * It provides methods:
 * <ul>
 * <li>{@link #getAll()}: Retrieves a list of all directors from the storage.</li>
 * <li>{@link #getById(Long)}: Retrieves a director by their ID.</li>
 * <li>{@link #save(Director)}: Saves a new director to the storage. Returns the saved director.</li>
 * <li>{@link #update(Director)}: Updates an existing director in the storage. Returns the updated director.</li>
 * <li>{@link #delete(Long)}: Deletes a director from the storage by their ID.</li>
 * <li>{@link #validateDirectorId(Long)}: Validates that a director ID is not null and exists in the storage. Throws an exception if not.</li>
 * </ul>
 *
 * @see Director
 * @see DirectorDto
 * @see Storage
 * @see DirectorDbStorage
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

  @Autowired
  private final Storage<Director> storage;

  public List<DirectorDto> getAll() {
    log.debug("Inside getAll method.");
    return storage.findAll().stream().map(DirectorMapper::mapToDirectorDto).toList();
  }

  public DirectorDto getById(Long id) {
    log.debug("Inside getByID method to get a director with ID = {}.", id);
    return DirectorMapper.mapToDirectorDto(storage.findById(id)
        .orElseThrow(() -> new NotFoundException("Director with ID = " + id + " doesn't exist.")));
  }

  public DirectorDto save(Director director) {
    return DirectorMapper.mapToDirectorDto(storage.save(director));
  }

  public DirectorDto update(Director newDirector) {
    return DirectorMapper.mapToDirectorDto(storage.update(newDirector));
  }

  public void delete(Long id) {
    storage.delete(id);
  }

  public void validateDirectorId(final Long id) {
    log.debug("Validating director id {} is not null and exist in DB", id);
    if (id == null || !storage.isExist(id)) {
      log.warn("Director with ID = {} not found in the db.", id);
      throw new NotFoundException("Director with ID = " + id + " not found.");
    }
    log.debug("Success in validating director id {} is not null and exist in DB", id);
  }
}
