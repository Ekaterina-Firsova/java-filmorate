package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.Optional;

/**
 * Interface for generic CRUD operations on a storage data for a specific type.
 *
 * @param <T> the type of the entity to handle
 */
public interface Storage<T> {

  /**
   * Saves an entity.
   *
   * @param t The entity to be saved. Must not be null.
   * @return The saved entity
   */
  T save(T t);

  /**
   * Retrieves an entity by its identifier.
   *
   * @param id – id the identifier of the entity, must not be null
   * @return an Optional containing the found entity or an empty Optional if no entity is found.
   */
  Optional<T> findById(Long id);

  /**
   * Returns all entities.
   *
   * @return an Iterable containing all entities
   */
  Collection<T> findAll();

  /**
   * Updates an entity.
   *
   * @param t the entity to be updated, must not be null
   * @return the updated entity
   */
  T update(T t);

  /**
   * Deletes an entity by its identifier.
   *
   * @param id the identifier of the entity to be deleted, must not be null
   */
  void delete(Long id);


}
