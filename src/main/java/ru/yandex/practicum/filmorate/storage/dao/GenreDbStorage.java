package ru.yandex.practicum.filmorate.storage.dao;

import java.util.Collection;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.ReadOnlyStorage;

/**
 * Implementation of the {@link GenreStorage} for managing {@link Genre} entities in the database.
 * <p> This class provides methods implementations for Read only operations on genre records.
 * <p> It extends BaseRepository for utilizing Spring's JdbcTemplate for database interactions.
 *
 * @see ReadOnlyStorage
 * @see GenreStorage
 */
@Repository
@Slf4j
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {

  private static final String FIND_ALL_QUERY = "SELECT * FROM genre ORDER BY id ASC";
  private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";
  private static final String EXIST_QUERY = "SELECT EXISTS(SELECT 1 FROM genre WHERE id = ?)";
  private static final String GET_FILM_GENRES_QUERY = """
      SELECT g.*
      FROM genre g
      RIGHT JOIN film_genre fg ON g.id = fg.genre_id
      WHERE fg.film_id = ?
      ORDER BY g.id ASC
      """;

  @Autowired
  public GenreDbStorage(final JdbcTemplate jdbc, final RowMapper<Genre> mapper) {
    super(jdbc, mapper);
  }

  @Override
  public Collection<Genre> findAll() {
    return findMany(FIND_ALL_QUERY);
  }

  @Override
  public Optional<Genre> findById(final Long id) {
    return findOne(FIND_BY_ID_QUERY, id);
  }

  @Override
  public boolean isExist(final Long id) {
    return checkExistence(EXIST_QUERY, id);
  }

  @Override
  public Collection<Genre> getGenresForFilm(final Long filmId) {
    return findMany(GET_FILM_GENRES_QUERY, filmId);
  }
}
