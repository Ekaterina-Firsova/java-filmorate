package ru.yandex.practicum.filmorate.service;

import java.util.Collection;

/**
 * A generic service interface for performing basic CRUD (Create, Read, Update, Delete) operations.
 * This interface defines the common operations that can be performed on entities of type
 * {@code T}.
 * <p>The CRUD operations included are:
 * <ul>
 *   <li>{@link #save(Object)} - Saves a new entity of type {@code T}.</li>
 *   <li>{@link #update(Object)} - Updates an existing entity of type {@code T}.</li>
 *   <li>{@link #getAll()} - Retrieves all entities of type {@code T}.</li>
 *   <li>{@link #getById(Long)} - Retrieves an entity of type {@code T} by its unique identifier.</li>
 * </ul>
 *
 * @param <T> The type of the entity that the service manages.
 */
public interface CrudService<T> {

  T save(T t);

  T update(T t);

  Collection<T> getAll();

  T getById(Long id);

}
