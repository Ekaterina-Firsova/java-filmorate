package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.List;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

/**
 * A service interface for managing film-related operations and interactions, including CRUD
 * operations, adding and removing likes, and retrieving films based on various criteria.
 * <p>
 * The service relies on the {@link FilmStorage} and {@link UserService} for data persistence and
 * user operations, respectively.
 * <p>
 * Methods include:
 * <ul>
 * <li>{@link #addLike(Long, Long)}: Adds a like to a film from a user.</li>
 * <li>{@link #removeById(Long)}: Removes a film from the DB by a given id.</li>
 * <li>{@link #getDirectorFilms(Long, String)}: Retrieves all films for a given director sorted by number of likes or release year.</li>
 * <li>{@link #getCommonFilms(Long, Long)}: Retrieves common films for two users sorted by its popularity.</li>
 * <li>{@link #search(String, String)}: Serches for films based on the specified query and search criteria.</li>
 * <li>{@link #getTopFilms(int, Long, Integer)}: Retrieves the top-rated films based on the number of likes.</li>
 * </ul>
 *
 * @see CrudService
 * @see FilmDto
 * @see FilmStorage
 * @see UserService
 * @see GenreStorage
 * @see MpaRatingStorage
 * @see EventService
 * @see DirectorService
 */
public interface FilmService extends CrudService<FilmDto> {

  FilmDto addLike(Long filmId, Long userId);

  FilmDto removeLike(Long filmId, Long userId);

  List<FilmDto> getDirectorFilms(Long id, String sortBy);

  Collection<Film> getCommonFilms(Long userId, Long friendId);

  List<FilmDto> search(String query, String by);

  List<FilmDto> getTopFilms(int count, Long genreId, Integer year);

}
