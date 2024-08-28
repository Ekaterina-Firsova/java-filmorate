package ru.yandex.practicum.filmorate.storage;

/**
 * Interface for generic CRUD operations on a storage data for a specific type.
 *
 * @param <T> the type of the entity to handle
 */
public interface Storage<T> extends ReadOnlyStorage<T> {

  /**
   * Saves an entity.
   *
   * @param t The entity to be saved. Must not be null.
   * @return The saved entity
   */
  T save(T t);

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
