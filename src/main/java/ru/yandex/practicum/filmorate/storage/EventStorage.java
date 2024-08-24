package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.dao.EventDbStorage;


/**
 * EventStorage interface for managing event data in the storage system.
 *
 * @see EventDbStorage
 */
public interface EventStorage {

    /**
     * Adds a new event to the storage.
     *
     * @param event The event to be added.
     * @return The added event with the new eventId.
     */
    Event addEvent(Event event);

    /**
     * Retrieves a collection of events associated with a specific user ID.
     *
     * @param userId the ID of the user whose events are to be retrieved, must not be null
     * @return a collection of events associated with the specified user
     */
    Collection<Event> findUserEvents(Long userId);
}
