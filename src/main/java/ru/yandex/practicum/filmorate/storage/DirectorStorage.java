package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDbStorage;

/**
 * Storage interface for managing {@link Director} entities.
 * <p>
 * This interface extends the generic {@link Storage} interface to provide CRUD operations specific
 * to {@link Director} entities.
 * </p>
 *
 * @see Director
 * @see Storage
 * @see ReadOnlyStorage
 * @see DirectorDbStorage
 */
public interface DirectorStorage extends Storage<Director> {

}
