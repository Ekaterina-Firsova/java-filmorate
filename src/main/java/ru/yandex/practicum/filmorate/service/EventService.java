package ru.yandex.practicum.filmorate.service;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.EventStorage;

/**
 * A service class that handles event-related operations and interactions.
 * <p>
 * This service is responsible for managing user-generated events such as adding friends, liking
 * films, and other user activities that need to be logged and retrieved as part of the event feed.
 * <p>
 * It provides methods:
 * <ul>
 *   <li>{@link #getFeed(Long)}: Retrieves the event feed for a specified user.</li>
 *   <li>{@link #logEvent(Long, Long, EventType, Operation)}: Logs a new event to the event storage.</li>
 * </ul>
 *
 * @see Event
 * @see EventDto
 * @see EventStorage
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {

  private final EventStorage eventStorage;

  public List<EventDto> getFeed(final Long userId) {
    log.debug("Inside getFeed for user with ID {} to fetch event feed ", userId);
    return eventStorage.findUserEvents(userId).stream().map(EventMapper::mapToEventDto).toList();
  }

  public void logEvent(final Long userId, final Long entityId, EventType type,
      Operation operation) {
    final Event event = Event.builder()
        .timestamp(Instant.now().toEpochMilli())
        .userId(userId)
        .eventType(type)
        .operation(operation)
        .entityId(entityId)
        .build();

    eventStorage.addEvent(event);
  }

}
