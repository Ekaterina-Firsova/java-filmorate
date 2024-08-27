package ru.yandex.practicum.filmorate.storage.dao;

import java.sql.Timestamp;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.EventStorage;

/**
 * Implementation of {@link EventStorage} for managing {@link Event} entities in the database.
 * <p>
 * This class provides methods to perform operations on event records, including adding new events
 * and retrieving all events associated with a specific user. It extends {@link BaseRepository} and
 * utilizes Spring's {@link JdbcTemplate} for database interactions.
 *
 * @see EventStorage
 * @see BaseRepository
 */
@Repository
@Slf4j
public class EventDbStorage extends BaseRepository<Event> implements EventStorage {

  private static final String INSERT_QUERY = """
      INSERT INTO event (event_type_id, operation_id, timestamp, user_id, entity_id)
      VALUES((SELECT et.ID FROM EVENT_TYPE et WHERE TYPE = ?),
         (SELECT o.ID FROM OPERATION o WHERE name = ?),
          ?,?,?)
      """;

  private static final String FIND_ALL_USER_EVENTS_QUERY = """
      SELECT e.*,
       et.TYPE AS event_type,
       o.NAME AS operation
      FROM EVENT e
      LEFT JOIN EVENT_TYPE et ON e.EVENT_TYPE_ID = et.ID
      LEFT JOIN OPERATION o ON e.OPERATION_ID = o.ID
      WHERE e.USER_ID = ?
      """;

  @Autowired
  public EventDbStorage(final JdbcTemplate jdbc, final RowMapper<Event> mapper) {
    super(jdbc, mapper);
  }

  @Override
  public void addEvent(final Event event) {
    log.debug("Inside 'addEvent' to add a new event with data: {}", event);
    final Long eventId = insert(INSERT_QUERY,
        event.getEventType().name(),
        event.getOperation().name(),
        new Timestamp(event.getTimestamp()),
        event.getUserId(),
        event.getEntityId());
    event.setEventId(eventId);
    log.debug("Event added with generated ID: {}", eventId);
  }

  @Override
  public Collection<Event> findUserEvents(final Long userId) {
    log.debug("Inside 'findUSerEvents' method to get feed for user with id {}", userId);
    return findMany(FIND_ALL_USER_EVENTS_QUERY, userId);
  }
}
