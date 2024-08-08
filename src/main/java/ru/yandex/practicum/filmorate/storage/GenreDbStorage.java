package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

/**
 * Implementation of the {@link GenreStorage} fro managing {@link Genre} entities in the database.
 * <p> This class provides methods implementations for Read only operations on genre records.
 * <p> It extends BaseRepository for utilizing Spring's JdbcTemplate for database interactions.
 * @see ReadOnlyStorage
 * @see GenreStorage
 */
@Repository
@Slf4j
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {

  private static final String FIND_ALL_QUERY = "SELECT * FROM genre ORDER BY id ASC";
  private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";

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

}
