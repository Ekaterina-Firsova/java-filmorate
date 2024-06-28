package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

/**
 * Controller class for managing Users in the Filmorate application. All endpoints in this
 * controller are relative to the base path "/users".
 */
@Slf4j
@RestController
@RequestMapping(UserController.USER)
public class UserController {

  public static final String USER = "/users";

  private final Map<Long, User> users = new HashMap<>();

  /**
   * Handles POST requests to add a new user.
   *
   * @param user - the user to be added
   * @return the added user
   */
  @PostMapping
  public User save(@Valid @RequestBody final User user) {
    log.info("Received request to add new user: {}", user);
    final User savedUser = saveUser(user);
    log.info("User added successfully: {}", savedUser);
    return savedUser;
  }

  /**
   * Handles PUT requests to update an existing user.
   *
   * @param newUser - the user with updated information
   * @return the updated user
   */
  @PutMapping
  public User update(@Valid @RequestBody final User newUser) {
    log.info("Received request to update user: {}", newUser);
    final User updatedUser = updateUser(newUser);
    log.info("User updated successfully: {}", updatedUser);
    return updatedUser;
  }

  /**
   * Handles GET requests to retrieve all users.
   *
   * @return a collection of all users
   */
  @GetMapping
  public Collection<User> getAll() {
    return users.values();
  }

  /* UserService */

  /**
   * Saves a new user to the collection.
   *
   * @param user the user to be saved
   * @return the saved user
   */
  User saveUser(final User user) {
    log.debug("Entering saveUser method.");
    checkDataDuplication(user);
    user.setId(getNextId());
    if (user.getName() == null || user.getName().isBlank()) {
      user.setName(user.getLogin());
    }
    users.put(user.getId(), user);
    return user;
  }

  /**
   * Updates an existing user in the collection.
   *
   * @param user the user with updated information
   * @return the  user information after the update
   */
  User updateUser(final User user) {
    log.debug("Entering updateUser method.");
    final Long id = user.getId();
    if (id == null) {
      throw new IllegalArgumentException("User ID must be provided.");
    }
    final User oldUser = users.get(id);
    log.debug("User before updating: {}", oldUser);
    if (oldUser == null) {
      throw new NotFoundException("User with ID " + id + " not found.");
    }
    checkDataDuplication(user);
    users.put(id, user);
    log.debug("User after updating: {}", user);
    return user;
  }

  /**
   * Checks for data duplication in the collection of users.
   *
   * @param user the user to check for duplication
   */
  private void checkDataDuplication(final User user) {
    Optional<User> duplicateUser = users.values().stream().filter(f -> f.equals(user)).findFirst();
    boolean isDuplicate = duplicateUser.isPresent();
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
    long currentMaxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
    return ++currentMaxId;
  }

}
