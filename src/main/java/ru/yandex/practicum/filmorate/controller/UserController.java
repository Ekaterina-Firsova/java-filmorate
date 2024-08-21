package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

/**
 * Controller class for managing Users in the Filmorate application. All endpoints in this
 * controller are relative to the base path "/users".
 */
@Slf4j
@RestController
@RequestMapping("/users")
@Validated
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
  public UserDto save(@Valid @RequestBody final UserDto user) {
    log.info("Received request POST /users with body: {}", user);
    final UserDto savedUser = userService.save(user);
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
  public UserDto update(@Valid @RequestBody final UserDto newUser) {
    log.info("Received request PUT /users with body: {}", newUser);
    final UserDto updatedUser = userService.update(newUser);
    log.info("User updated successfully: {}", updatedUser);
    return updatedUser;
  }

  /**
   * Handles PUT request to adds a friend to the user with the specified IDs.
   *
   * @param id       The ID of the user who is adding a friend. Must not be null.
   * @param friendId The ID of the friend to be added. Must not be null.
   * @return The updated user with the added friend.
   */
  @PutMapping("/{id}/friends/{friendId}")
  public UserDto addFriend(@PathVariable("id") @NotNull final Long id,
      @PathVariable("friendId") @NotNull final Long friendId) {
    log.info("Received request PUT users/{}/friends/{}", id, friendId);
    final UserDto user = userService.addFriend(id, friendId);
    log.info("Adding friend to the user is successful: {}", user);
    return user;
  }

  /**
   * Handles GET requests to retrieve all users.
   *
   * @return a collection of all users
   */
  @GetMapping
  public Collection<UserDto> getAll() {
    log.info("Received request GET users");
    return userService.getAll();
  }

  /**
   * Handles GET requests to retrieve friends of a user by their ID.
   *
   * @param id The ID of the user whose friends are to be retrieved. Must not be null.
   * @return The list of friends of the user with the specified ID.
   */
  @GetMapping("/{id}/friends")
  public List<UserDto> getFriendsByUserId(@PathVariable @NotNull final Long id) {
    log.info("Received request GET users/{}/friends", id);
    return userService.getUserFriends(id);
  }

  /**
   * Handles GET request to retrieve the list of mutual friends between two users.
   *
   * @param id      The ID of the first user.
   * @param otherId The ID of the second user to compare friends with.
   * @return The List of mutual friends between the two users..
   */
  @GetMapping("/{id}/friends/common/{otherId}")
  public List<UserDto> getMutualFriends(@PathVariable("id") @NotNull final Long id,
      @PathVariable("otherId") @NotNull final Long otherId) {
    log.info("Received request GET users/{}/friends/common/{}", id, otherId);
    return userService.getMutualFriends(id, otherId);
  }

  /**
   * Handles DELETE request to remove a specified friend from the user's friend list.
   *
   * @param id       The ID of the user who wants to remove a friend.
   * @param friendId The ID of the friend to be removed.
   * @return The updated user data.
   */
  @DeleteMapping("/{id}/friends/{friendId}")
  public UserDto deleteFriend(@PathVariable("id") @NotNull final Long id,
      @PathVariable("friendId") @NotNull final Long friendId) {
    log.info("Received request DELETE users/{}/friends/{}", id, friendId);
    return userService.removeFriend(id, friendId);
  }

  @DeleteMapping("/{id}")
  public void deleteById(@PathVariable("id") @NotNull final Long id) {
    log.info("Received request DELETE user/{}", id);
    userService.removeById(id);
  }

}
