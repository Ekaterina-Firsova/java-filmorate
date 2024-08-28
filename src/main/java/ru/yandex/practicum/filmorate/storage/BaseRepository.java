package ru.yandex.practicum.filmorate.storage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.rowmappers.UserRowMapper;

/**
 * An abstract base repository providing common data access methods for CRUD operations. This class
 * utilizes Spring's JdbcTemplate for database interactions and provides methods:
 * <ul>
 *   <li>{@link #insert(String, Object...)}: Inserts a new record and returns the generated key.</li>
 *   <li>{@link #insertCompositePk(String, Object...)}: Inserts a new record into a table with a composite primary key and validates key generation.</li>
 *   <li>{@link #update(String, Object...)}: Updates an existing record based on the given query and parameters, and validated that at least one row was affected by the update operation.</li>
 *   <li>{@link #findMany(String, Object...)}: Retrieves multiple records based on the given query and parameters.</li>
 *   <li>{@link #findOne(String, Object...)}: Retrieves a single record based on the given query and parameters.</li>
 *   <li>{@link #delete(String, Object...)}: Deletes a record based on the given query and identifier. Returns {@code true} in case of success of deleting operation.</li>
 * </ul>
 *
 * @param <T> The type of the entity managed by this repository.
 * @see UserDbStorage
 * @see UserRowMapper
 */
@Slf4j
@RequiredArgsConstructor
public abstract class BaseRepository<T> {

  protected final JdbcTemplate jdbc;
  protected final RowMapper<T> mapper;

  protected long insert(final String query, Object... params) {
    log.debug("Executing insert with query: {} and parameters: {}", query, params);
    final GeneratedKeyHolder keyHolder = insertData(query, params);
    return Optional.ofNullable(keyHolder.getKeyAs(Long.class))
        .orElseThrow(() -> {
          log.warn("Data saving failed: No ID obtained for the entity.");
          return new InternalServerException("Data saving failed: No ID obtained.");
        });
  }

  protected void insertCompositePk(final String query, Object... params) {
    log.debug("Executing insert for Table with Composite PK, query: {} and parameters: {}", query,
        params);
    final GeneratedKeyHolder keyHolder = insertData(query, params);
    final Map<String, Object> keys = keyHolder.getKeys();
    if (keys == null || keys.isEmpty()) {
      log.warn("Insert operation failed: No keys returned: {}", keys);
      throw new InternalServerException("Data saving failed: no keys returned.");
    }
    log.debug("Insert successful, keys returned, {}", keys.values());
  }

  protected void update(final String query, Object... params) {
    log.debug("Executing update with query: {} and parameters: {}", query, params);
    final int rowUpdated = jdbc.update(query, params);
    if (rowUpdated == 0) {
      log.warn("Update failed: No rows affected for query: {} with parameters: {}", query, params);
      throw new InternalServerException("Data update failed: No rows affected.");
    }
  }

  protected Collection<T> findMany(final String query, Object... params) {
    log.debug("Executing findMany with query: {} and parameters: {}", query, params);
    Collection<T> c = jdbc.query(query, mapper, params);
    return c;
  }

  protected Optional<T> findOne(final String query, Object... params) {
    log.debug("Executing findOne with query: {} and parameters: {}", query, params);
    try {
      final T result = jdbc.queryForObject(query, mapper, params);
      log.debug("FindOne returned result: {}", result);
      return Optional.ofNullable(result);
    } catch (EmptyResultDataAccessException ignored) {
      log.debug("FindOne returned no result for query: {} and parameters: {}", query, params);
      return Optional.empty();
    }
  }

  protected boolean delete(final String query, Object... ids) {
    log.debug("Executing delete with query: {} and id: {}", query, ids);
    final int rowDeleted = jdbc.update(query, ids);
    log.debug("Rows affected after deleting: {}", rowDeleted);
    return rowDeleted > 0;
  }

  protected boolean checkExistence(final String query, Object... params) {
    try {
      return Boolean.TRUE.equals(jdbc.queryForObject(query, Boolean.class, params));
    } catch (EmptyResultDataAccessException ignored) {
      return false;
    }
  }

  private GeneratedKeyHolder insertData(final String query, Object... params) {
    final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbc.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          query, Statement.RETURN_GENERATED_KEYS);
      for (int idx = 0; idx < params.length; idx++) {
        ps.setObject(idx + 1, params[idx]);
      }
      return ps;
    }, keyHolder);
    return keyHolder;
  }

}
