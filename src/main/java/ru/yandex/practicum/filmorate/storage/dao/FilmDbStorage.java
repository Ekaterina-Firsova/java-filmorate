package ru.yandex.practicum.filmorate.storage.dao;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

/**
 * Implementation of {@link FilmStorage} for managing {@link Film} entities in the database.
 * <p> This class provides methods to perform CRUD operations on films records
 * It extends {@link BaseRepository} and utilizes Spring's {@link JdbcTemplate} for database
 * interactions.
 *
 * @see Storage
 * @see FilmStorage
 * @see BaseRepository
 */
@Repository("filmDbStorage")
@Slf4j
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

  private static final String INSERT_QUERY = """
      INSERT INTO film (name, description, release_date, duration, mpa_rating_id)
      VALUES (?, ?, ?, ?, ?)
      """;
  private static final String INSERT_GENRE_QUERY = """
      INSERT INTO film_genre (film_id, genre_id)
      VALUES (?, ?)
      """;
  private static final String INSERT_LIKE_QUERY = """
      INSERT INTO user_like (film_id, user_id)
      VALUES (?, ?)
      """;
  private static final String UPDATE_FILM_QUERY = """
      UPDATE film SET
      name = ?,
      description = ?,
      release_date = ?,
      duration =? ,
      mpa_rating_id = ?
      WHERE id = ?
      """;
  private static final String FIND_BY_ID = """
      SELECT f.*,
             mr.NAME AS mpa_name,
             array_agg( g.ID) AS genre_id,
             array_agg( g.NAME)AS genre_name,
             array_agg( ul.USER_ID) AS like_id
      FROM film f
      LEFT JOIN MPA_RATING mr ON f.MPA_RATING_ID = mr.ID
      LEFT JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID
      LEFT JOIN GENRE g ON fg.GENRE_ID = g.ID
      LEFT JOIN USER_LIKE ul ON ul.FILM_ID = f.ID
      WHERE f.id = ?
      GROUP BY f.ID, mr.NAME
      """;
  private static final String GET_TOP_LIKED_FILMS_QUERY = """
      SELECT f.*,
            mr.NAME AS mpa_name,
            array_agg(g.ID) AS genre_id,
            array_agg(g.NAME) AS genre_name,
            array_agg(ul.USER_ID) AS like_id,
            COUNT(DISTINCT ul.USER_ID) AS like_count
      FROM film f
      LEFT JOIN MPA_RATING mr ON f.MPA_RATING_ID = mr.ID
      LEFT JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID
      LEFT JOIN GENRE g ON fg.GENRE_ID = g.ID
      LEFT JOIN USER_LIKE ul ON ul.FILM_ID = f.ID
      GROUP BY f.ID, mr.NAME
      ORDER BY like_count DESC
      LIMIT ?
      """;
  private static final String GET_ALL_WITH_GENRES_LIKES = """
      SELECT f.*,
             mr.NAME AS mpa_name,
             array_agg( g.ID) AS genre_id,
             array_agg( g.NAME)AS genre_name,
             array_agg( ul.USER_ID) AS like_id
      FROM film f
      LEFT JOIN MPA_RATING mr ON f.MPA_RATING_ID = mr.ID
      LEFT JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID
      LEFT JOIN GENRE g ON fg.GENRE_ID = g.ID
      LEFT JOIN USER_LIKE ul ON ul.FILM_ID = f.ID
      GROUP BY f.ID, mr.NAME
      """;
  private static final String EXIST_QUERY = "SELECT EXISTS(SELECT 1 FROM film WHERE id = ?)";
  private static final String DELETE_BY_ID_QUERY = "DELETE FROM film WHERE id =?";
  private static final String REMOVE_GENRES_QUERY = "DELETE from film_genre WHERE film_id = ?";
  private static final String REMOVE_LIKE_QUERY = "DELETE FROM user_like WHERE film_id = ? AND user_id = ?";

  @Autowired
  public FilmDbStorage(final JdbcTemplate jdbc, final RowMapper<Film> mapper) {
    super(jdbc, mapper);
  }


  @Override
  public Film save(final Film film) {
    log.debug("Inside 'save' method to add a new record about film to the db: {}", film);
    final Long id = insert(
        INSERT_QUERY,
        film.getName(),
        film.getDescription(),
        Date.valueOf(film.getReleaseDate()),
        film.getDuration(),
        film.getMpa().getId()
    );
    film.setId(id);
    insertGenresToDb(film);
    log.debug("Film saved with ID: {}.", id);
    return film;
  }

  @Override
  public Film update(final Film film) {
    log.debug("Inside 'update' method to change a film record with data: {}", film);
    update(UPDATE_FILM_QUERY,
        film.getName(),
        film.getDescription(),
        Date.valueOf(film.getReleaseDate()),
        film.getDuration(),
        film.getMpa().getId(),
        film.getId()
    );
    updateGenres(film);
    log.debug("Film updated.");
    return film;
  }

  @Override
  public Collection<Film> findAll() {
    log.debug("Inside 'findAll' method to get data for all films available.");
    return findMany(GET_ALL_WITH_GENRES_LIKES);
  }

  @Override
  public Optional<Film> findById(Long id) {
    log.debug("Inside 'findById' method to get data for film with ID = {}.", id);
    return findOne(FIND_BY_ID, id);
  }

  @Override
  public void delete(final Long id) {
    delete(DELETE_BY_ID_QUERY, id);
  }

  @Override
  public boolean isExist(Long id) {
    return checkExistence(EXIST_QUERY, id);
  }

  @Override
  public List<Film> getTopFilms(final int count) {
    log.debug("Inside 'getTopFilms' method to get a list of top {} liked films.", count);
    return findMany(GET_TOP_LIKED_FILMS_QUERY, count).stream().toList();
  }

  @Override
  public Film addLike(final Long filmId, final Long userId) {
    log.debug("Inside 'addLike' method to save like from user {} for the film {}.", userId, filmId);
    insertCompositePk(INSERT_LIKE_QUERY, filmId, userId);
    return findById(filmId).orElseThrow(
        () -> new NotFoundException("Film with Id = " + filmId + "not found."));
  }

  @Override
  public Film removeLike(final Long filmId, final Long userId) {
    delete(REMOVE_LIKE_QUERY, filmId, userId);
    return findById(filmId).orElseThrow(
        () -> new NotFoundException("Film with Id = " + filmId + "not found."));
  }

  @Override
  public void removeById(Long id) {
    log.debug("Inside 'removeById' method: removing film with id = {}", id);
    delete(DELETE_BY_ID_QUERY, id);
  }


  private void insertGenresToDb(final Film film) {
    if (film.getGenres().isEmpty()) {
      return;
    }
    film.getGenres().forEach(genre ->
        insertCompositePk(INSERT_GENRE_QUERY, film.getId(), genre.getId()));
  }

  private void updateGenres(final Film film) {
    log.debug("Updating genres for film {}.", film);
    if (film.getGenres().isEmpty()) {
      return;
    }
    delete(REMOVE_GENRES_QUERY, film.getId());
    insertGenresToDb(film);
  }

}
