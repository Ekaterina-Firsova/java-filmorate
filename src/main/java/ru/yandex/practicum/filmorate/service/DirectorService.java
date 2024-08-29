package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.DirectorDto;

/**
 * A service interface for managing director-related operations.
 * <p>
 * This interface extends {@link CrudService} and provides additional methods for managing
 * directors, including validating director IDs and handling CRUD operations specific to directors.
 * <p>
 * Methods included:
 * <ul>
 *   <li>{@link #validateDirectorId(Long)}: Validates that a director ID is not null and exists in the storage.</li>
 * </ul>
 *
 * @see DirectorDto
 * @see DirectorServiceImpl
 * @see CrudService
 */
public interface DirectorService extends CrudService<DirectorDto> {

  void validateDirectorId(final Long id);

}
