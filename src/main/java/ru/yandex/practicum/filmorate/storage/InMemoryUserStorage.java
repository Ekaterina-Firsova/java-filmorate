package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

  private final Map<Long, User> users = new HashMap<>();
  private Long lastId = 0L;

  @Override
  public User save(final User user) {
    checkDataDuplication(user);
    user.setId(getNextId());
    users.put(user.getId(), user);
    return user;
  }

  @Override
  public Optional<User> findById(final Long id) {
    return Optional.ofNullable(users.get(id));
  }

  @Override
  public Collection<User> findAll() {
    return users.values();
  }

  @Override
  public User update(final User user) {
    final Long id = user.getId();
    checkDataDuplication(user);
    users.put(id, user);
    return user;
  }

  @Override
  public void delete(final Long id) {
    users.remove(id);
  }

  @Override
  public boolean isExist(Long id) {
    return users.get(id) != null;
  }

  /**
   * Checks for data duplication in the collection of users. Verifies whether the given user already
   * exists in the collection of users, excluding itself if it is already present (based on ID
   * comparison). If a duplicate is found (a user with the same data but different ID), a
   * {@link DuplicatedDataException} is thrown.
   *
   * @param user the film to check for duplication
   * @throws DuplicatedDataException if the action would result in duplication
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
