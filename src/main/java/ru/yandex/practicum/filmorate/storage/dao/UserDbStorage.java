package ru.yandex.practicum.filmorate.storage.dao;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.Storage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

/**
 * Implementation of {@link UserStorage} for managing {@link User} entities in the database.
 * <p> This class provides methods to perform CRUD operations on user records and manage user
 * relationships. It extends {@link BaseRepository} and utilizes Spring's {@link JdbcTemplate} for
 * database interactions.
 *
 * @see Storage
 * @see UserStorage
 * @see BaseRepository
 */
@Repository("userDbStorage")
@Slf4j
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    private static final String INSERT_QUERY = """
            INSERT INTO "user" (login, name, email, birthday)
            VALUES(?,?,?,?)
            """;
    private static final String UPDATE_QUERY = """
            UPDATE "user" SET
            login = ?,
            name = ?,
            email = ?,
            birthday = ?
            WHERE id = ?
            """;
    private static final String FIND_ALL_QUERY = """
            SELECT u.*,
            array_agg(DISTINCT f.FRIEND_ID) AS friend
            FROM "user" u
            LEFT JOIN FRIENDSHIP f ON u.ID = f.ID
            GROUP BY u.ID
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT u.*,
            array_agg(DISTINCT f.FRIEND_ID) AS friend
            FROM "user" u
            LEFT JOIN FRIENDSHIP f ON u.ID = f.ID
            WHERE u.id = ?
            GROUP BY u.ID
            """;
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM \"user\" WHERE id = ?";
    private static final String ADD_FRIEND_QUERY = "INSERT INTO friendship (id, friend_id) VALUES(?,?)";
    private static final String GET_FRIENDS_QUERY =
            """
                    SELECT  u.*, array_agg(f2.FRIEND_ID) AS friend
                    FROM FRIENDSHIP f
                    LEFT JOIN "user" u ON f.friend_id = u.id
                    LEFT JOIN FRIENDSHIP f2 ON u.id = f2.id
                    LEFT JOIN "user" u2 ON f2.FRIEND_ID = u2.id
                    WHERE f.id = ?
                    Group BY u.id
                    """;
    private static final String REMOVE_FRIEND_QUERY = "DELETE FROM friendship WHERE id = ? AND friend_id = ?";
    private static final String EXIST_QUERY = "SELECT EXISTS(SELECT 1 FROM \"user\" WHERE id = ?)";

    @Autowired
    public UserDbStorage(final JdbcTemplate jdbc, final RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User save(final User user) {
        log.debug("Inside 'save' method to add a new record of user to the db: {}", user);
        final Long id = insert(
                INSERT_QUERY,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                Date.valueOf(user.getBirthday())
        );
        user.setId(id);
        log.debug("User saved with ID: {}", id);
        return user;
    }

    @Override
    public User update(final User user) {
        log.debug("Inside 'update' to update a user with data: {}", user);
        update(
                UPDATE_QUERY,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );
        log.debug("User updated.");
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<User> findById(final Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public void delete(final Long id) {
        delete(DELETE_BY_ID_QUERY, id);
    }

    @Override
    public User addFriend(Long id, Long friendId) {
        log.debug(
                "Inside 'addFriend' method: user with id = {} is adding a friend with id = {}", id,
                friendId);
        insertCompositePk(ADD_FRIEND_QUERY, id, friendId);
        return findById(id).orElseThrow(
                () -> new NotFoundException("User not found with ID: " + id));
    }

    @Override
    public List<User> getFriends(Long id) {
        return findMany(GET_FRIENDS_QUERY, id).stream().toList();
    }

    @Override
    public void removeFriend(Long id, Long friendId) {
        log.debug("Inside 'removeFriend' method: user with id = {} is removing a friend with id = {}",
                id, friendId);
        delete(REMOVE_FRIEND_QUERY, id, friendId);
    }

    @Override
    public boolean isExist(final Long id) {
        return checkExistence(EXIST_QUERY, id);
    }

    public void removeById(final Long id) {
        log.debug("Inside 'removeById' method: removing user with id = {}", id);
        delete(DELETE_BY_ID_QUERY, id);
    }

    @Override
    public Long getSimilarUser(Long userId) {
        Optional<User> similarUser = findOne(
                "SELECT u.* " +
                        "FROM user_like u1 " +
                        "JOIN user_like u2 ON u1.film_id = u2.film_id AND u1.user_id <> u2.user_id " +
                        "JOIN \"user\" u ON u2.user_id = u.id " +
                        "WHERE u1.user_id = ?" +
                        "GROUP BY u2.user_id " +
                        "ORDER BY COUNT(*) DESC " +
                        "LIMIT 1", userId);

        return similarUser.map(User::getId).orElse(null);
    }
}
