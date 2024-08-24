package ru.yandex.practicum.filmorate.storage.rowmappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Operation;

/**
 * A RowMapper implementation for mapping rows from {@link ResultSet} to {@link Event} instance
 */
@Component
public class EventRowMapper implements RowMapper<Event> {

  @Override
  public Event mapRow(ResultSet rs, int rowNum) throws SQLException {

    return Event.builder()
        .eventId(rs.getLong("id"))
        .timestamp(rs.getTimestamp("timestamp").getTime())
        .userId(rs.getLong("user_id"))
        .eventType(EventType.valueOf(rs.getString("event_type")))
        .operation(Operation.valueOf(rs.getString("operation")))
        .entityId(rs.getLong("entity_id"))
        .build();
  }
}
