package ru.yandex.practicum.filmorate.storage;

import java.util.List;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.in_memory.InMemoryUserStorage;

/**
 * UserStorage interface for managing user data in the storage system. This interface extends the
 * generic Storage interface with specific methods for handling user-related operations.
 *
 * @see Storage
 * @see InMemoryUserStorage
 * @see UserDbStorage
 */
public interface UserStorage extends Storage<User> {

    /**
     * Adds a friend to a user's friend list.
     *
     * @param id       The ID of the user who is adding a friend.
     * @param friendId The ID of the friend being added.
     * @return The updated user with the new friend added.
     * @throws NotFoundException if either the user or the friend does not exist.
     */
    User addFriend(Long id, Long friendId);

    /**
     * Removes a friend from a user's friend list.
     *
     * @param id       The ID of the user who is removing a friend.
     * @param friendId The ID of the friend being removed.
     * @throws NotFoundException if either the user or the friend does not exist.
     */
    void removeFriend(Long id, Long friendId);

    /**
     * Retrieves the list of friends - User objects, for a specific user.
     *
     * @param id The ID of the user whose friends are being retrieved.
     * @return A list of users who are friends with the specified user.
     * @throws NotFoundException if the user does not exist.
     */
    List<User> getFriends(Long id);

    Long getSimilarUser(Long userId);
}
