package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

/**
 * Controller class for managing Users in the Filmorate application. All endpoints in this
 * controller are relative to the base path "/users".
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  /**
   * Handles POST requests to add a new user.
   *
   * @param user - the user to be added
   * @return the added user
   */
  @PostMapping
  public User save(@Valid @RequestBody final User user) {
    log.info("Received request POST /users with body: {}", user);
    final User savedUser = userService.save(user);
    log.info("User successfully added: {}", savedUser);
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
    log.info("Received request PUT /users with body: {}", newUser);
    final User updatedUser = userService.update(newUser);
    log.info("User updated successfully: {}", updatedUser);
    return updatedUser;
  }

  /**
   * Handles PUT request to adds a friend to the user with the specified IDs.
   * @param id The ID of the user who is adding a friend. Must not be null.
   * @param friendId The ID of the friend to be added. Must not be null.
   * @return The updated user with the added friend.
   */
  @PutMapping("/{id}/friends/{friendId}")
  public User addFriend(@PathVariable("id") final Long id, @PathVariable("friendId") final Long friendId) {
    log.info("Received request PUT users/{}/friends/{}",id, friendId);
    final User user = userService.addFriend(id,friendId);
    log.info("Adding friend to the user is successful: {}",user);
    return user;
  }

  /**
   * Handles GET requests to retrieve all users.
   *
   * @return a collection of all users
   */
  @GetMapping
  public Collection<User> getAll() {
    return userService.getAll();
  }

  /**
   * Handles GET requests to retrieve friends of a user by their ID.
   *
   * @param id The ID of the user whose friends are to be retrieved. Must not be null.
   * @return The list of friends of the user with the specified ID.
   */
  @GetMapping("/{id}/friends")
  public List<User> getFriendsByUserId(@PathVariable final Long id) {
    return userService.getUserFriends(id);
  }

  @GetMapping("/{id}/friends/common/{otherId}")
  public List<User> getMutualFriends(@PathVariable("id") final Long id, @PathVariable("otherId") final Long otherId) {
    return userService.getMutualFriends(id,otherId);
  }

  @DeleteMapping("/{id}/friends/{friendId}")
  public User deleteFriend(@PathVariable("id") final Long id, @PathVariable("friendId") final Long friendId) {
    return userService.removeFriend(id,friendId);
  }

}
