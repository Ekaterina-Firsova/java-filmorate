package ru.yandex.practicum.filmorate.model;

/**
 * Enum representing the possible operations that can be performed during an event.
 * <ul>
 *     <li>{@link #ADD}: Represents an operation where an entity is added (e.g., a friend, like, or review).</li>
 *     <li>{@link #UPDATE}: Represents an operation where an entity is updated (e.g., a review).</li>
 *     <li>{@link #REMOVE}: Represents an operation where an entity is removed (e.g., a friend, like or review).</li>
 * </ul>
 */
public enum Operation {
  ADD,
  UPDATE,
  REMOVE
}
