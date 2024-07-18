package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

/**
 * A service class that handles user-related operations and interactions.
 * <p>
 * The service relies on an underlying storage mechanism provided by {@link UserStorage}.
 * <p>
 * It provides methods
 * <ul>
 * <li>{@link #save(User)}: Saves a new user to the storage.</li>
 * <li>{@link #update(User)}: Updates an existing user in the storage.</li>
 * <li>{@link #getAll()}: Retrieves all users from the storage.</li>
 * <li>{@link #getById(Long)}: Retrieves a user by their ID.</li>
 * <li>{@link #addFriend(Long, Long)}: Adds a friend to the user's friend list.</li>
 * <li>{@link #getUserFriends(Long)}: Retrieves a list of a user's friends.</li>
 * <li>{@link #getMutualFriends(Long, Long)}: Retrieves a list of mutual friends between two users.</li>
 * <li>{@link #removeFriend(Long, Long)}: Removes a friend from the user's friend list.</li>
 * </ul>
 *
 * @see User
 * @see UserStorage
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements CrudService<User> {

  private final UserStorage userStorage;

  @Override
  public User save(final User user) {
    log.debug("Inside save method to safe a user {}", user);
    checkAndSetUserName(user);
    return userStorage.save(user);
  }

  @Override
  public User update(final User user) {
    log.debug("Inside update method to update a user with data: {}", user);
    final Long id = user.getId();
    validateUserId(id);
    checkAndSetUserName(user);
    return userStorage.update(user);
  }

  @Override
  public Collection<User> getAll() {
    log.debug("Inside getAll method");
    return userStorage.findAll();
  }

  @Override
  public User getById(Long id) {
    log.debug("Inside getByID method to get a user with ID = {}", id);
    return getUserByIdOrThrow(id);
  }


  public User addFriend(final Long userId, final Long newFriendId) {
    log.debug("Adding friends to both: {} and {}", userId, newFriendId);
    final User user = getUserByIdOrThrow(userId);
    final User newFriend = getUserByIdOrThrow(newFriendId);

    user.getFriends().add(newFriendId);
    newFriend.getFriends().add(userId);
    return user;
  }

  public List<User> getUserFriends(final Long id) {
    log.debug("Inside getUserFriends for the ID = {}", id);
    final User user = getUserByIdOrThrow(id);
    return user.getFriends().stream()
        .map(this::getUserByIdOrThrow)
        .toList();
  }

  public List<User> getMutualFriends(final Long userOneId, final Long userTwoId) {
    log.debug("Inside getMutualFriends for userd with ID {} and {}", userOneId, userTwoId);
    final User userOne = getUserByIdOrThrow(userOneId);
    final User userTwo = getUserByIdOrThrow(userTwoId);

    Set<Long> mutualFriendsIds = new HashSet<>(userOne.getFriends());
    mutualFriendsIds.retainAll(userTwo.getFriends());
    log.debug("Getting mutual friend for users1 friends {} and user2 friends {} and getting {}",
        userOne.getFriends(), userTwo.getFriends(), mutualFriendsIds);

    return mutualFriendsIds.stream()
        .map(this::getUserByIdOrThrow)
        .toList();
  }

  public User removeFriend(final Long userId, final Long friendToRemoveId) {
    log.debug("Inside removeFriend to remove from user with ID {} a friend with ID {} ", userId,
        friendToRemoveId);
    final User user = getUserByIdOrThrow(userId);
    final User friendToRemove = getUserByIdOrThrow(friendToRemoveId);

    user.getFriends().remove(friendToRemoveId);
    friendToRemove.getFriends().remove(userId);
    return user;
  }

  private User getUserByIdOrThrow(final Long id) {
    log.debug("Getting a user instance for ID = {} from the storage", id);
    return userStorage.findById(id)
        .orElseThrow(() -> new NotFoundException("User with ID = " + id + " not found."));
  }

  private void validateUserId(final Long id) {
    log.debug("Validating user id {} is not null and exist in DB", id);
    if (id == null || userStorage.findById(id).isEmpty()) {
      throw new NotFoundException("User with ID = " + id + " not found.");
    }
  }

  private void checkAndSetUserName(final User user) {
    if (user.getName() == null || user.getName().isEmpty()) {
      user.setName(user.getLogin());
      log.debug("Property name was assigned with Login property value: {}", user.getName());
    }
  }

}
