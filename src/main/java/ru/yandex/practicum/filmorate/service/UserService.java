package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

/**
 * A service class that handles user-related operations and interactions.
 * <p>
 * The service relies on an underlying storage mechanism provided by {@link UserStorage} and manages
 * user-related actions such as saving, updating, and retrieving users. It also handles user
 * relationships, like adding or removing friends, and logs these events using
 * {@link EventService}.
 * <p>
 * It provides methods:
 * <ul>
 * <li>{@link #save(UserDto)}: Saves a new user to the storage. Returns saved user with generated ID.</li>
 * <li>{@link #update(UserDto)}: Updates an existing user in the storage. Returns updated user data.</li>
 * <li>{@link #getAll()}: Retrieves all users from the storage.</li>
 * <li>{@link #getById(Long)}: Retrieves a user by their ID.</li>
 * <li>{@link #addFriend(Long, Long)}: Adds a friend to the user's friend list.</li>
 * <li>{@link #getUserFriends(Long)}: Retrieves a list of a user's friends.</li>
 * <li>{@link #getMutualFriends(Long, Long)}: Retrieves a list of mutual friends between two users.</li>
 * <li>{@link #removeFriend(Long, Long)}: Removes a friend from the user's friend list.</li>
 * <li>{@link #removeById(Long)}: Removes a user by their ID.</li>
 * <li>{@link #getFeed(Long)}: Retrieves a list of events related to the user.</li>
 * <li>{@link #getUserRecommendations(long)}: Retrieves recommendations for a user based on their preferences and interactions.</li>
 * <li>{@link #validateUserId(Long)}: validated if user with given id exists in the DB, throws exception if not.</li>
 * </ul>
 *
 * @see User
 * @see UserDto
 * @see UserStorage
 * @see FilmStorage
 * @see EventService
 */
@Service
@Slf4j
public class UserService implements CrudService<UserDto> {

  private final UserStorage userStorage;
  private final FilmStorage filmStorage;
  private final EventService eventService;

  @Autowired
  public UserService(@Qualifier("userDbStorage") final UserStorage userStorage,
      @Qualifier("filmDbStorage") final FilmStorage filmStorage,
      final EventService eventService) {
    this.userStorage = userStorage;
    this.filmStorage = filmStorage;
    this.eventService = eventService;
  }

  @Override
  public UserDto save(final UserDto userDto) {
    log.debug("Inside save method to safe a user {}", userDto);
    final User user = UserMapper.mapToUser(userDto);
    checkAndSetUserName(user);
    return UserMapper.mapToUserDto(userStorage.save(user));
  }

  @Override
  public UserDto update(final UserDto userDto) {
    log.debug("Inside update method to update a user with data: {}", userDto);
    validateUserId(userDto.getId());
    final User user = UserMapper.mapToUser(userDto);
    checkAndSetUserName(user);
    return UserMapper.mapToUserDto(userStorage.update(user));
  }

  @Override
  public Collection<UserDto> getAll() {
    log.debug("Inside getAll method");
    return userStorage.findAll().stream()
        .map(UserMapper::mapToUserDto).toList();
  }

  @Override
  public UserDto getById(final Long id) {
    log.debug("Inside getByID method to get a user with ID = {}", id);
    return UserMapper.mapToUserDto(getUserByIdOrThrow(id));
  }

  public UserDto addFriend(final Long userId, final Long newFriendId) {
    log.debug("Inside 'addFriend' method: user with id = {} is adding a friend with id = {}",
        userId, newFriendId);
    validateUserId(userId);
    validateUserId(newFriendId);
    if (userId.equals(newFriendId)) {
      throw new IllegalArgumentException("User cannot be friends with themselves.");
    }
    final User user = userStorage.addFriend(userId, newFriendId);
    log.debug("User with id {} added successfully a friend with id {}",
        userId, newFriendId);

    eventService.addEvent(userId, newFriendId, EventType.FRIEND, Operation.ADD);

    return UserMapper.mapToUserDto(user);
  }

  public List<UserDto> getUserFriends(final Long id) {
    log.debug("Inside getUserFriends for the ID = {}", id);
    validateUserId(id);
    return userStorage.getFriends(id).stream().map(UserMapper::mapToUserDto).toList();
  }

  public List<UserDto> getMutualFriends(final Long userOneId, final Long userTwoId) {
    log.debug("Inside getMutualFriends for user with ID {} and {}", userOneId, userTwoId);
    validateUserId(userOneId);
    validateUserId(userTwoId);
    final List<User> userOneFriends = userStorage.getFriends(userOneId);
    final List<User> userTwoFriends = userStorage.getFriends(userTwoId);

    final Set<User> mutualFriendsIds = new HashSet<>(userOneFriends);
    mutualFriendsIds.retainAll(userTwoFriends);
    log.debug("Getting mutual friend for users1 friends {} and user2 friends {} and getting {}",
        userOneFriends, userTwoFriends, mutualFriendsIds);
    return mutualFriendsIds.stream()
        .map(UserMapper::mapToUserDto)
        .toList();
  }

  public UserDto removeFriend(final Long userId, final Long friendToRemoveId) {
    log.debug("Inside removeFriend to remove from user with ID {} a friend with ID {} ", userId,
        friendToRemoveId);
    validateUserId(userId);
    validateUserId(friendToRemoveId);

    userStorage.removeFriend(userId, friendToRemoveId);
    log.debug("User with id {} removed a friend with id {} successfully",
        userId, friendToRemoveId);

    eventService.addEvent(userId, friendToRemoveId, EventType.FRIEND, Operation.REMOVE);

    return UserMapper.mapToUserDto(getUserByIdOrThrow(userId));
  }

  public void removeById(final Long userId) {
    log.debug("Deleting user with ID {} ", userId);
    validateUserId(userId);
    userStorage.delete(userId);
  }

  public List<EventDto> getFeed(final Long userId) {
    log.debug("Inside getFeed for user with ID {} to fetch event feed ", userId);
    validateUserId(userId);
    return eventService.getFeed(userId);
  }

  private User getUserByIdOrThrow(final Long id) {
    log.debug("Getting a user instance for ID = {} from the storage", id);
    return userStorage.findById(id)
        .orElseThrow(() -> new NotFoundException("User with ID = " + id + " not found."));
  }

  public void validateUserId(final Long id) {
    log.debug("Validating user id {} is not null and exist in DB", id);
    if (id == null || !userStorage.isExist(id)) {
      log.warn("User with ID = {} not found in storage.", id);
      throw new NotFoundException("User with ID = " + id + " not found.");
    }
    log.debug("Success in validating user id {} is not null and exist in DB", id);
  }

  private void checkAndSetUserName(final User user) {
    if (user.getName() == null || user.getName().isEmpty()) {
      user.setName(user.getLogin());
      log.debug("Property name was assigned with Login property value: {}", user.getName());
    }
  }

  public Collection<FilmDto> getUserRecommendations(final long userId) {
    log.debug("Getting recommendations films for user with ID = {}", userId);
    validateUserId(userId);
    Long similarUserId = userStorage.getSimilarUser(userId);
    if (similarUserId == null) {
      return List.of();
    }
    Collection<Film> films = filmStorage.getRecommendedFilms(userId, similarUserId);
    return films.stream()
        .map(FilmMapper::mapToFilmDto)
        .collect(Collectors.toList());
  }

}
