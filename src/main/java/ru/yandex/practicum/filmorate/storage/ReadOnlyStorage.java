package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.Optional;

/**
 * Interface for read operations on a storage data for a specific type.
 *
 * @param <T> the type of the entity to handle.
 */
public interface ReadOnlyStorage<T> {

  /**
   * Retrieves an entity by its identifier.
   *
   * @param id the identifier of the entity, must not be null
   * @return an Optional containing the found entity or an empty Optional if no entity is found.
   */
  Optional<T> findById(Long id);

  /**
   * Returns all entities.
   *
   * @return a Collection containing all entities
   */
  Collection<T> findAll();
}