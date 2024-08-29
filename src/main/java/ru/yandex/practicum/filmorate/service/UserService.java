package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.List;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

/**
 * A service interface for managing user-related operations and interactions.
 * <p>
 * This interface extends {@link CrudService} and adds user-specific operations, such as managing
 * friends, and retrieving user recommendations and event feeds.
 * <p>
 * Methods include:
 * <ul>
 *   <li>{@link #addFriend(Long, Long)}: Adds a friend to the user’s friend list.</li>
 *   <li>{@link #getUserFriends(Long)}: Retrieves a list of friends for a specified user.</li>
 *   <li>{@link #getMutualFriends(Long, Long)}: Retrieves a list of mutual friends between two users.</li>
 *   <li>{@link #removeFriend(Long, Long)}: Removes a friend from the user’s friend list.</li>
 *   <li>{@link #getUserRecommendations(long)}: Retrieves recommendations for a user based on their preferences and interactions.</li>
 *   <li>{@link #getFeed(Long)}: Retrieves a list of events related to a specified user.</li>
 *   <li>{@link #validateUserId(Long)}: Validates if a user with the given ID exists in the storage, throwing an exception if not.</li>
 * </ul>
 *
 * @see CrudService
 * @see UserDto
 * @see UserStorage
 * @see FilmStorage
 * @see EventService
 */
public interface UserService extends CrudService<UserDto> {

  UserDto addFriend(Long userId, Long friendId);

  List<UserDto> getUserFriends(Long id);

  List<UserDto> getMutualFriends(Long id, Long otherId);

  UserDto removeFriend(Long userId, Long friendId);

  Collection<FilmDto> getUserRecommendations(long userId);

  List<EventDto> getFeed(Long id);

  void validateUserId(Long id);


}
