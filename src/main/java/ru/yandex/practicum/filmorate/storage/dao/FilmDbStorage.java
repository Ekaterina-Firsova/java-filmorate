package ru.yandex.practicum.filmorate.storage.dao;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InvalidDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SearchCriteria;
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
  private static final String ADD_LIKE_QUERY = """
      MERGE INTO user_like (film_id, user_id)
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
             array_agg( ul.USER_ID) AS like_id,
             array_agg( d.ID) AS director_id,
             array_agg( d.NAME)AS director_name
      FROM film f
      LEFT JOIN MPA_RATING mr ON f.MPA_RATING_ID = mr.ID
      LEFT JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID
      LEFT JOIN GENRE g ON fg.GENRE_ID = g.ID
      LEFT JOIN USER_LIKE ul ON ul.FILM_ID = f.ID
      LEFT JOIN DIRECTOR_FILM df ON df.FILM_ID = f.ID
      LEFT JOIN DIRECTOR d ON df.DIRECTOR_ID = d.ID
      WHERE f.id = ?
      GROUP BY f.ID, mr.NAME
      """;
  private static final String GET_TOP_LIKED_FILMS_QUERY = """
      SELECT f.*,
            mr.NAME AS mpa_name,
            array_agg(g.ID) AS genre_id,
            array_agg(g.NAME) AS genre_name,
            array_agg(ul.USER_ID) AS like_id,
            array_agg( d.ID) AS director_id,
            array_agg( d.NAME)AS director_name,
            COUNT(DISTINCT ul.USER_ID) AS like_count
      FROM film f
      LEFT JOIN MPA_RATING mr ON f.MPA_RATING_ID = mr.ID
      LEFT JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID
      LEFT JOIN GENRE g ON fg.GENRE_ID = g.ID
      LEFT JOIN USER_LIKE ul ON ul.FILM_ID = f.ID
      LEFT JOIN DIRECTOR_FILM df ON df.FILM_ID = f.ID
      LEFT JOIN DIRECTOR d ON df.DIRECTOR_ID = d.ID
      GROUP BY f.ID, mr.NAME
      ORDER BY like_count DESC
      LIMIT ?
      """;
  private static final String GET_ALL_WITH_GENRES_LIKES = """
      SELECT f.*,
             mr.NAME AS mpa_name,
             array_agg( g.ID) AS genre_id,
             array_agg( g.NAME)AS genre_name,
             array_agg( ul.USER_ID) AS like_id,
             array_agg( d.ID) AS director_id,
             array_agg( d.NAME)AS director_name
      FROM film f
      LEFT JOIN MPA_RATING mr ON f.MPA_RATING_ID = mr.ID
      LEFT JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID
      LEFT JOIN GENRE g ON fg.GENRE_ID = g.ID
      LEFT JOIN USER_LIKE ul ON ul.FILM_ID = f.ID
      LEFT JOIN DIRECTOR_FILM df ON df.FILM_ID = f.ID
      LEFT JOIN DIRECTOR d ON df.DIRECTOR_ID = d.ID
      GROUP BY f.ID, mr.NAME
      """;
  private static final String EXIST_QUERY = "SELECT EXISTS(SELECT 1 FROM film WHERE id = ?)";
  private static final String DELETE_BY_ID_QUERY = "DELETE FROM film WHERE id =?";
  private static final String REMOVE_GENRES_QUERY = "DELETE from film_genre WHERE film_id = ?";
  private static final String REMOVE_LIKE_QUERY = "DELETE FROM user_like WHERE film_id = ? AND user_id = ?";
  private static final String INSERT_DIRECTOR_QUERY = "INSERT INTO director_film (director_id, film_id) VALUES (?, ?)";
  private static final String SELECT_ALL_DIRECTORS_FILM_BY_LIKE = """
      SELECT f.*,
             mr.NAME AS mpa_name,
             array_agg( g.ID) AS genre_id,
             array_agg( g.NAME)AS genre_name,
             array_agg( ul.USER_ID) AS like_id,
             array_agg( d.ID) AS director_id,
             array_agg( d.NAME)AS director_name,
             COUNT(DISTINCT ul.USER_ID) AS like_count
      FROM film f
      LEFT JOIN MPA_RATING mr ON f.MPA_RATING_ID = mr.ID
      LEFT JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID
      LEFT JOIN GENRE g ON fg.GENRE_ID = g.ID
      LEFT JOIN USER_LIKE ul ON ul.FILM_ID = f.ID
      LEFT JOIN DIRECTOR_FILM df ON df.FILM_ID = f.ID
      LEFT JOIN DIRECTOR d ON df.DIRECTOR_ID = d.ID
      WHERE d.ID = ?
      GROUP BY f.ID, mr.NAME
      ORDER BY like_count DESC
      """;
  private static final String SELECT_ALL_DIRECTORS_FILM_BY_YEAR = """
      SELECT f.*,
             mr.NAME AS mpa_name,
             array_agg( g.ID) AS genre_id,
             array_agg( g.NAME)AS genre_name,
             array_agg( ul.USER_ID) AS like_id,
             array_agg( d.ID) AS director_id,
             array_agg( d.NAME)AS director_name
      FROM film f
      LEFT JOIN MPA_RATING mr ON f.MPA_RATING_ID = mr.ID
      LEFT JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID
      LEFT JOIN GENRE g ON fg.GENRE_ID = g.ID
      LEFT JOIN USER_LIKE ul ON ul.FILM_ID = f.ID
      LEFT JOIN DIRECTOR_FILM df ON df.FILM_ID = f.ID
      LEFT JOIN DIRECTOR d ON df.DIRECTOR_ID = d.ID
      WHERE d.id = ?
      GROUP BY f.ID, mr.NAME
      ORDER BY f.release_date
      """;
  private static final String REMOVE_DIRECTOR_QUERY = "DELETE from director_film WHERE film_id = ?";
  private static final String SEARCH_BY_ONE_CRITERIA_START = """
      SELECT f.*,
      	     mr.NAME AS mpa_name,
      	     array_agg( g.ID) AS genre_id,
      	     array_agg( g.NAME)AS genre_name,
      	     array_agg( ul.USER_ID) AS like_id,
      	     array_agg( d.ID) AS director_id,
      	     array_agg( d.NAME)AS director_name
      FROM film f
      LEFT JOIN MPA_RATING mr ON f.MPA_RATING_ID = mr.ID
      LEFT JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID
      LEFT JOIN GENRE g ON fg.GENRE_ID = g.ID
      LEFT JOIN USER_LIKE ul ON ul.FILM_ID = f.ID
      LEFT JOIN DIRECTOR_FILM df ON df.FILM_ID = f.ID
      LEFT JOIN DIRECTOR d ON df.DIRECTOR_ID = d.ID
      WHERE
      """;
  private static final String SEARCH_BY_ONE_CRITERIA_END = """
      GROUP BY f.ID, mr.NAME
      ORDER BY count(ul.USER_ID) desc
      """;

  private final GenreDbStorage genreStorage;

  @Autowired
  public FilmDbStorage(final JdbcTemplate jdbc, final RowMapper<Film> mapper, GenreDbStorage genreStorage) {
    super(jdbc, mapper);
    this.genreStorage = genreStorage;
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
    insertDirectorToDb(film);
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
    updateDirector(film);
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
  public List<Film> getTopFilms(final int count, final Long genreId, final Integer year) {
      log.debug("Getting top {} liked films.", count);
      return findMany(GET_TOP_LIKED_FILMS_QUERY, count).stream()
              .filter(film -> genreId == null || film.getGenres()
                      .contains(genreStorage.findById(genreId).orElse(null)))
              .filter(film -> year == null || film.getReleaseDate().getYear() == year)
              .toList();
  }

  @Override
  public Film addLike(final Long filmId, final Long userId) {
    log.debug("Inside 'addLike' method to save like from user {} for the film {}.", userId, filmId);
    insertCompositePk(ADD_LIKE_QUERY, filmId, userId);
    return findById(filmId).orElseThrow(
            () -> new NotFoundException("Film with Id = " + filmId + "not found."));
  }

  @Override
  public Film removeLike(final Long filmId, final Long userId) {
    delete(REMOVE_LIKE_QUERY, filmId, userId);
    return findById(filmId).orElseThrow(
            () -> new NotFoundException("Film with Id = " + filmId + "not found."));
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
    delete(REMOVE_GENRES_QUERY, film.getId());
    if (film.getGenres().isEmpty()) {
      return;
    }
    insertGenresToDb(film);
  }

  private void insertDirectorToDb(final Film film) {
    log.debug("insert director for film {}.", film);
    if (film.getDirectors().isEmpty()) {
      return;
    }
    film.getDirectors().forEach(director ->
            insertCompositePk(INSERT_DIRECTOR_QUERY, director.getId(), film.getId()));
  }

  private void updateDirector(final Film film) {
    log.info("Updating director for film {}.", film);
    delete(REMOVE_DIRECTOR_QUERY, film.getId());
    if (film.getDirectors().isEmpty()) {
      return;
    }
    insertDirectorToDb(film);
  }

  @Override
  public List<Film> getDirectorFilms(final Long id, final String sortBy) {
    log.info("Get director's film with ID - {}, sorted by - {}", id, sortBy);
    return switch (sortBy) {
      case "likes" -> findMany(SELECT_ALL_DIRECTORS_FILM_BY_LIKE, id).stream().toList();
      case "year" -> findMany(SELECT_ALL_DIRECTORS_FILM_BY_YEAR, id).stream().toList();
      default -> throw new NotFoundException(String.format("Sorted by %s not exist", sortBy));
    };
  }

  @Override
  public Collection<Film> getRecommendedFilms(Long userId, Long similarUserId) {
    String query = """
                  SELECT f.*,
                         mr.NAME AS mpa_name,
                         array_agg( g.ID) AS genre_id,
                         array_agg( g.NAME)AS genre_name,
                         array_agg( ul.USER_ID) AS like_id,
                         array_agg( d.ID) AS director_id,
                         array_agg( d.NAME)AS director_name
                  FROM film f
                  JOIN user_like ul1 ON f.id = ul1.film_id AND ul1.user_id = ?
                  LEFT JOIN user_like ul2 ON f.id = ul2.film_id AND ul2.user_id =?
                  LEFT JOIN MPA_RATING mr ON f.MPA_RATING_ID = mr.ID
                  LEFT JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID
                  LEFT JOIN GENRE g ON fg.GENRE_ID = g.ID
                  LEFT JOIN USER_LIKE ul ON ul.FILM_ID = f.ID
                  LEFT JOIN DIRECTOR_FILM df ON df.FILM_ID = f.ID
                  LEFT JOIN DIRECTOR d ON df.DIRECTOR_ID = d.ID
                  WHERE ul2.user_id IS NULL
                  GROUP BY f.ID, mr.NAME;
            """;

    return findMany(query, similarUserId, userId);
  }

  @Override
  public Collection<Film> getCommonFilms(Long userId, Long friendId) {
    String query = """
                       SELECT f.*,
                                        mr.NAME AS mpa_name,
                                        array_agg( g.ID) AS genre_id,
                                        array_agg( g.NAME)AS genre_name,
                                        array_agg( ul.USER_ID) AS like_id,
                                        array_agg( d.ID) AS director_id,
                                        array_agg( d.NAME)AS director_name
                      FROM film f
                      JOIN user_like ul1 ON f.id = ul1.film_id
                      JOIN user_like ul2 ON f.id = ul2.film_id
                                        LEFT JOIN MPA_RATING mr ON f.MPA_RATING_ID = mr.ID
                                        LEFT JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID
                                        LEFT JOIN GENRE g ON fg.GENRE_ID = g.ID
                                        LEFT JOIN USER_LIKE ul ON ul.FILM_ID = f.ID
                                        LEFT JOIN DIRECTOR_FILM df ON df.FILM_ID = f.ID
                                        LEFT JOIN DIRECTOR d ON df.DIRECTOR_ID = d.ID
                      WHERE ul1.user_id = ? AND ul2.user_id = ?
                      GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id
                      ORDER BY (SELECT COUNT(*) FROM user_like ul WHERE ul.film_id = f.id) DESC;
                      """;

    return findMany(query, userId, friendId);
  }


  @Override
  public List<Film> searchBy(final String query, final List<SearchCriteria> searchCriterias) {
    log.info("Searching films by query{} and criteria list {}.", query, searchCriterias);

    final String whereClause = searchCriterias.stream()
        .map(sc -> buildWhereClause(sc, query))
        .collect(Collectors.joining(" OR "));

    return findMany(buildSearchByQuery(whereClause)).stream().toList();
  }

  private String buildWhereClause(final SearchCriteria searchCriteria, final String query) {
    log.debug("Building filter for the WHERE clause using criteria {} and partial text {}",
        searchCriteria, query);
    return String.format(" LOWER(%s) LIKE LOWER('%%%s%%')", searchCriteria.getTableColumn(), query);
  }

  private String buildSearchByQuery(final String whereClause) {
    log.debug("Assembling a query to search films by uniting parts start + where clause {} + end.",
        whereClause);
    return SEARCH_BY_ONE_CRITERIA_START + whereClause + SEARCH_BY_ONE_CRITERIA_END;
  }

  private String getTableName(final String searchCriteria) {
    log.debug("Defining table name that corresponds search criteria {}.", searchCriteria);
    return switch (searchCriteria) {
      case "title" -> "f.NAME";
      case "director" -> "d.NAME";
      default -> throw new InvalidDataException(
          String.format("Incorrect search criteria %s", searchCriteria));
    };
  }
}
