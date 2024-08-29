package ru.yandex.practicum.filmorate.service;

import java.util.List;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

/**
 * A service interface for managing user events in the application.
 * <p>
 * This service provides methods for retrieving a user's event feed and adding new events related to
 * user actions (such as likes, reviews, or friendships).
 * </p>
 *
 * <ul>
 *   <li>{@link #getFeed(Long)} - Retrieves the event feed for a specified user.</li>
 *   <li>{@link #addEvent(Long, Long, EventType, Operation)} - Adds a new event to the storage,
 *       representing a user action.</li>
 * </ul>
 *
 * @see EventDto
 * @see EventType
 * @see Operation
 */
public interface EventService {

  List<EventDto> getFeed(Long userId);

  void addEvent(Long userId, Long entityId, EventType type, Operation operation);

}
