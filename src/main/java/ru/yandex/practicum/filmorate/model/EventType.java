package ru.yandex.practicum.filmorate.model;

/**
 * Enum representing the types of events that can occur on the platform.
 * <ul>
 *     <li>{@link #FRIEND}: Represents a friend-related event, such as adding or removing a friend.</li>
 *     <li>{@link #LIKE}: Represents a like-related event, such as liking a film or removing like.</li>
 *     <li>{@link #REVIEW}: Represents a review-related event, such as posting, updating or deleting a review.</li>
 * </ul>
 */
public enum EventType {
  FRIEND,
  LIKE,
  REVIEW
}
