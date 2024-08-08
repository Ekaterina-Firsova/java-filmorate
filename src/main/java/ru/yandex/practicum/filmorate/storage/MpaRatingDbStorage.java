package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

/**
 * Implementation of the {@link MpaRatingStorage} for managing {@link MpaRating} entities in the
 * database.
 * <p> This class provides methods implementations for Read only operations on mpa records.
 * <p> It extends BaseRepository for utilizing Spring's JdbcTemplate for database interactions.
 *
 * @see ReadOnlyStorage
 * @see MpaRatingStorage
 */
@Repository
@Slf4j
public class MpaRatingDbStorage extends BaseRepository<MpaRating> implements MpaRatingStorage {

  private static final String FIND_ALL_QUERY = "SELECT * FROM mpa_rating ORDER BY id";
  private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa_rating WHERE id = ?";

  @Autowired
  public MpaRatingDbStorage(JdbcTemplate jdbc,
      RowMapper<MpaRating> mapper) {
    super(jdbc, mapper);
  }

  @Override
  public Collection<MpaRating> findAll() {
    return findMany(FIND_ALL_QUERY);
  }

  @Override
  public Optional<MpaRating> findById(final Long id) {
    return findOne(FIND_BY_ID_QUERY, id);
  }

}
