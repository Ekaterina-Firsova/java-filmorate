package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.Event;

/**
 * Utility class for mapping between {@link Event} entity and {@link EventDto}.
 */
@UtilityClass
public class EventMapper {

  public EventDto mapToEventDto(final Event event) {
    return EventDto.builder()
        .eventId(event.getEventId())
        .timestamp(event.getTimestamp())
        .userId(event.getUserId())
        .eventType(event.getEventType())
        .operation(event.getOperation())
        .entityId(event.getEntityId())
        .build();
  }
}
