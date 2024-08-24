package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Represents an event that occurred within the platform, such as adding/deleting a friend, liking a
 * film, or posting/updating a review.
 */
@Data
@Builder
public class Event {

  /**
   * The unique identifier of the event.
   */
  private Long eventId;

  /**
   * The timestamp when the event occurred.
   */
  @NotNull
  private Long timestamp;

  /**
   * The ID of the user associated with the event.
   */
  @NotNull
  private Long userId;

  /**
   * The type of event, such as LIKE, REVIEW, or FRIEND.
   */
  @NotNull
  private EventType eventType;


  /**
   * The operation performed during the event, such as ADD, REMOVE, or UPDATE.
   */
  @NotNull
  private Operation operation;

  /**
   * The ID of the entity related to the event (e.g., a film, friend(user), or a review).
   */
  @NotNull
  private Long entityId;

}
