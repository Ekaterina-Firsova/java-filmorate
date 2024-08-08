package ru.yandex.practicum.filmorate.storage.db;

import java.sql.Date;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
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
      SELECT f.*, m.name AS mpa_name
      FROM film f
      LEFT JOIN mpa_rating m ON f.mpa_rating_id = m.id
      WHERE f.id = ?
      """;
  private static final String FIND_ALL_QUERY = """
      SELECT f.*, m.name AS mpa_name
      FROM film f
      LEFT JOIN mpa_rating m ON f.mpa_rating_id = m.id
      ORDER BY f.id ASC
      """;
  private static final String GET_LIKES_QUERY = """
      SELECT user_id FROM user_like WHERE film_id = ?
      """;
  private static final String GET_TOP_LIKED_FILMS_QUERY = """
      SELECT f.*, m.name AS mpa_name
      FROM film f
      LEFT JOIN mpa_rating m ON f.mpa_rating_id = m.id
      LEFT JOIN user_like ul ON f.id = ul.film_id
      GROUP BY f.id
      ORDER BY COUNT(ul.*) DESC
      LIMIT ?
      """;
  private static final String EXIST_QUERY = "SELECT EXISTS(SELECT 1 FROM film WHERE id = ?)";
  private static final String DELETE_BY_ID_QUERY = "DELETE FROM film WHERE id =?";
  private static final String REMOVE_GENRES_QUERY = "DELETE from film_genre WHERE film_id = ?";
  private static final String REMOVE_LIKE_QUERY = "DELETE FROM user_like WHERE film_id = ? AND user_id = ?";

  private final GenreStorage genreStorage;

  @Autowired
  public FilmDbStorage(final JdbcTemplate jdbc, final RowMapper<Film> mapper,
      final GenreStorage genreStorage, GenreStorage genreStorage1) {
    super(jdbc, mapper);
    this.genreStorage = genreStorage1;
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
    Collection<Film> allfilms = findMany(FIND_ALL_QUERY);
    allfilms.forEach(film -> {
      getGenresForFilm(film.getId()).forEach(g -> film.getGenres().add(g));
      getLikesForFilm(film.getId()).forEach(l -> film.getLikes().add(l));
    });
    return allfilms;
  }

  @Override
  public Optional<Film> findById(Long id) {
    log.debug("Inside 'findById' method to get data for film with ID = {}.", id);
    Optional<Film> filmOptional = findOne(FIND_BY_ID, id);
    filmOptional.ifPresent(film -> {
      getGenresForFilm(id).forEach(g -> film.getGenres().add(g));
      getLikesForFilm(id).forEach(l -> film.getLikes().add(l));
    });
    return filmOptional;
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
  public List<Film> getTopFilms(int count) {
    log.debug("Inside 'getTopFilms' method to get a list of top {} liked films.", count);
    List<Film> topFilms = findMany(GET_TOP_LIKED_FILMS_QUERY, count).stream().toList();
    topFilms.forEach(film -> {
      getGenresForFilm(film.getId()).forEach(g -> film.getGenres().add(g));
      getLikesForFilm(film.getId()).forEach(l -> film.getLikes().add(l));
    });
    return topFilms;
  }

  @Override
  public Film addLike(Long filmId, Long userId) {
    log.debug("Inside 'addLike' method to save like from user {} for the film {}.", userId, filmId);
    insertCompositePk(INSERT_LIKE_QUERY, filmId, userId);
    return findById(filmId).orElseThrow(
        () -> new NotFoundException("Film with Id = " + filmId + "not found."));
  }

  @Override
  public Film removeLike(Long filmId, Long userId) {
    delete(REMOVE_LIKE_QUERY, filmId, userId);
    return findById(filmId).orElseThrow(
        () -> new NotFoundException("Film with Id = " + filmId + "not found."));
  }

  private Set<Long> getLikesForFilm(final Long filmId) {
    return new HashSet<>(jdbc.query(GET_LIKES_QUERY,
        (rs, rowNum) -> rs.getLong("user_id"), filmId));
  }

  private Set<Genre> getGenresForFilm(final Long filmId) {
    return new HashSet<>(genreStorage.getGenresForFilm(filmId));
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
