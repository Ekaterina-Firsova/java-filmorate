package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

  private final Map<Long, User> users = new HashMap<>();
  private Long lastId = 0l;

  @Override
  public User save(final User user) {
    log.debug("Entering saveUser method.");
    checkDataDuplication(user);
    user.setId(getNextId());
    users.put(user.getId(), user);
    return user;
  }

  @Override
  public Optional<User> findById(Long id) {
    return Optional.ofNullable(users.get(id));
  }

  @Override
  public Collection<User> findAll() {
    return users.values();
  }

  @Override
  public User update(final User user) {
    log.debug("Entering updateUser method.");
    final Long id = user.getId();
    checkDataDuplication(user);
    users.put(id, user);
    log.debug("User after updating: {}", user);
    return user;
  }

  @Override
  public void delete(Long id) {
    users.remove(id);
  }

  /**
   * Checks for data duplication in the collection of users.
   *
   * @param user the user to check for duplication
   */
  private void checkDataDuplication(final User user) {
    boolean isDuplicate = users.values().stream()
        .anyMatch(existingUser ->
            !existingUser.getId().equals(user.getId()) && existingUser.equals(user));

    log.debug("Duplication of Data is found: {}", isDuplicate);
    if (isDuplicate) {
      throw new DuplicatedDataException("Action would result in duplication.");
    }
  }

  /**
   * Generates the next available user ID.
   *
   * @return the next available user ID
   */
  private long getNextId() {
    return ++lastId;
  }




}
