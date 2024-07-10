package ru.yandex.practicum.filmorate.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

/**
 * A service class  handles operations with users including  adding friends, removing friends, and
 * displaying a list of mutual friends.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements CrudService<User> {

  private final UserStorage userStorage;

  @Override
  public User save(final User user) {
    checkAndSetUserName(user);
    return userStorage.save(user);
  }

  @Override
  public User update(final User user) {
    final Long id = user.getId();
    validateUserId(id);
    checkAndSetUserName(user);
    return userStorage.update(user);
  }

  @Override
  public Collection<User> getAll() {
    return userStorage.findAll();
  }

  public User addFriend(final Long userId, final Long newFriendId) {
    final User user = findUserById(userId);
    final User newFriend =findUserById(newFriendId);

    user.getFriends().add(newFriendId);
    newFriend.getFriends().add(userId);
    return user;
  }

  public List<User> getUserFriends(final Long id) {
    final User user = findUserById(id);

    return user.getFriends().stream()
        .map(this::findUserById)
        .toList();
  }

  public List<User> getMutualFriends(final Long userOneId, final Long userTwoId) {
    final User userOne = findUserById(userOneId);
    final User userTwo = findUserById(userTwoId);

    Set<Long> mutualFriendsIds = new HashSet<>(userOne.getFriends());
    mutualFriendsIds.retainAll(userTwo.getFriends());

    return mutualFriendsIds.stream()
        .map(this::findUserById)
        .toList();
  }

  public User removeFriend(final Long userId, final Long friendToRemoveId) {
    final User user = findUserById(userId);
    final User friendToRemove = findUserById(friendToRemoveId);

    user.getFriends().remove(friendToRemoveId);
    friendToRemove.getFriends().remove(userId);
    return user;
  }

  private User findUserById(final Long id) {
    return userStorage.findById(id)
        .orElseThrow(() -> new NotFoundException("User with ID = " + id + " not found."));
  }

  private void validateUserId(final Long id) {
    if (id == null || userStorage.findById(id).isEmpty()) {
      throw new NotFoundException("User with ID = " + id + " not found.");
    }
  }

  private void checkAndSetUserName(final User user) {
    if (user.getName() == null || user.getName().isBlank()) {
      user.setName(user.getLogin());
      log.debug("Property name was assigned with Login filed value: {}", user.getName());
    }
  }

}
