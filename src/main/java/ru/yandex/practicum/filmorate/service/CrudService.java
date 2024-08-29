package ru.yandex.practicum.filmorate.service;

/**
 * A generic service interface for performing basic CRUD (Create, Read, Update, Delete) operations.
 * This interface extends {@link ReadService} and adds additional CRUD-specific operations. This
 * interface defines the common operations that can be performed on entities of type {@code T}.
 * <p>The CRUD operations included are:
 * <ul>
 *   <li>{@link #save(Object)} - Saves a new entity of type {@code T}.</li>
 *   <li>{@link #update(Object)} - Updates an existing entity of type {@code T}.</li>
 *   <li>{@link #removeById(Long)}: Removes an entity by its ID.</li>
 * </ul>
 *
 * @param <T> The type of the entity that the service manages.
 */
public interface CrudService<T> extends ReadService<T> {

  T save(T t);

  T update(T t);

  void removeById(Long id);

}
