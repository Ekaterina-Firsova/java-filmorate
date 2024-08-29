package ru.yandex.practicum.filmorate.service;

import java.util.Collection;

/**
 * A generic service interface for performing READ operations. This interface defines common
 * operations for retrieving entities of type {@code T}.
 * <p>
 * The READ operations included are:
 * <ul>
 *   <li>{@link #getAll()} - Retrieves all entities.</li>
 *   <li>{@link #getById(Long)} - Retrieves an entity by its unique identifier.</li>
 * </ul>
 *
 * @param <T> The type of the entity that the service manages.
 */
public interface ReadService<T> {

  Collection<T> getAll();

  T getById(Long id);

}
