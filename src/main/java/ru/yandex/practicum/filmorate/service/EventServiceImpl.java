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
 * A service implementation for managing user events.
 * <p>
 * This class provides concrete implementations for the methods defined in the {@link EventService}
 * interface, using an underlying {@link EventStorage} to manage event persistence.
 *
 * @see Event
 * @see EventDto
 * @see EventStorage
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

  private final EventStorage eventStorage;

  public List<EventDto> getFeed(final Long userId) {
    log.debug("Inside getFeed for user with ID {} to fetch event feed ", userId);
    return eventStorage.findUserEvents(userId)
        .stream()
        .map(EventMapper::mapToEventDto)
        .toList();
  }

  public void addEvent(final Long userId, final Long entityId, final EventType type,
      final Operation operation) {
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
