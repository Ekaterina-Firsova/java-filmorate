package ru.yandex.practicum.filmorate.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.Storage;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDbStorage;

/**
 * A service class for managing director-related operations.
 * <p>
 * This service interacts with the underlying {@link Storage} mechanism to perform CRUD operations
 * and manage directors. It provides methods to retrieve, save, update, and delete directors.
 * </p>
 * It provides the following methods:
 * <ul>
 *   <li>{@link #getAll()}: Retrieves a list of all directors from the storage.</li>
 *   <li>{@link #getById(Long)}: Retrieves a director by their ID. Throws a {@link NotFoundException} if not found.</li>
 *   <li>{@link #save(DirectorDto)}: Saves a new director to the storage. Returns the saved director.</li>
 *   <li>{@link #update(DirectorDto)}: Updates an existing director in the storage. Returns the updated director.</li>
 *   <li>{@link #removeById(Long)}: Deletes a director from the storage by their ID.</li>
 *   <li>{@link #validateDirectorId(Long)}: Validates that a director ID is not null and exists in the storage. Throws an exception if not.</li>
 * </ul>
 *
 * @see Director
 * @see DirectorDto
 * @see Storage
 * @see DirectorDbStorage
 */
@Slf4j
@Service
public class DirectorServiceImpl implements DirectorService {

  private final Storage<Director> storage;

  @Autowired
    public DirectorServiceImpl(Storage<Director> storage) {
        this.storage = storage;
    }

    @Override
  public List<DirectorDto> getAll() {
    log.debug("Inside getAll method.");
    return storage.findAll().stream()
            .map(DirectorMapper::mapToDirectorDto)
            .toList();
  }

  @Override
  public DirectorDto getById(Long id) {
    log.debug("Inside getByID method to get a director with ID = {}.", id);
    return DirectorMapper.mapToDirectorDto(storage.findById(id)
        .orElseThrow(() -> new NotFoundException("Director with ID = " + id + " doesn't exist.")));
  }

  @Override
  public DirectorDto save(DirectorDto director) {
    return DirectorMapper.mapToDirectorDto(storage.save(DirectorMapper.mapToDirector(director)));
  }

  @Override
  public DirectorDto update(DirectorDto newDirector) {
    return DirectorMapper.mapToDirectorDto(
        storage.update(DirectorMapper.mapToDirector(newDirector)));
  }

  @Override
  public void removeById(Long id) {
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
