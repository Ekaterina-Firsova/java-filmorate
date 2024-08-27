package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Operation;


/**
 * Data Transfer Object representing an Event.
 *
 * @see Event
 */
@Data
@Builder
public class EventDto {

  private Long eventId;

  @NotNull
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long timestamp;

  @NotNull
  private Long userId;

  @NotNull
  private EventType eventType;

  @NotNull
  private Operation operation;

  @NotNull
  private Long entityId;

}
