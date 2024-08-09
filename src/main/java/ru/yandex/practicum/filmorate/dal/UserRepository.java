package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class UserRepository extends BaseRepository<User> {
    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET name = ?, email = ?, birthday = ?, login = ? WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(name, email, birthday, login) " +
            "VALUES (?, ?, ?, ?)";

    // Инициализируем репозиторий
    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public List<User> findAll() {
        List<User> users = findMany(FIND_ALL_USERS_QUERY);
        for (User user : users) {
            Set<Long> friendsIds = getALLFriendsIds(user.getId());
            user.setFriends(friendsIds);
        }
        return users;
    }

    public Set<Long> getALLFriendsIds(long id) {
        return new HashSet<>(findManyIds("SELECT id_friend FROM friends WHERE id_user = ?", id));
    }

    public void updateFriendsStatus(Long userId, Long friendId, boolean status) {
        Set<Long> ids = getALLFriendsIds(friendId);
        if (ids != null && ids.contains(userId)) {
            update("UPDATE friends SET confirmed = ? WHERE id_user = ? AND id_friend = ?", status, userId, friendId);
        }
    }

    public Optional<User> findByEmail(String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }

    public Optional<User> findById(long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getLogin(),
                user.getId()
        );
        return user;
    }

    public User save(User user) {
        int id = insert(
                INSERT_QUERY,
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getLogin()
        );
        user.setId((long) id);
        return user;
    }

    public User addFriend(User user, long friendId, boolean status) {
        insert(
                "INSERT INTO friends(ID_USER, ID_FRIEND, CONFIRMED) VALUES (?,?,?)",
                user.getId(),
                friendId,
                status
        );
        return user;
    }

    public Collection<User> getFriends(long userId) {
        return findMany(
                "SELECT USERS.*" +
                        " FROM PUBLIC.FRIENDS" +
                        " LEFT JOIN PUBLIC.users ON FRIENDS.ID_FRIEND = users.ID" +
                        " WHERE FRIENDS.id_user = ?", userId);
    }

    public Optional<User> getUser(long userId) {
        return findOne("SELECT * FROM users WHERE ID =?", userId);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        List<User> commonFriends = findMany(
                "SELECT u.*" +
                        " FROM FRIENDS f1" +
                        " JOIN FRIENDS f2 ON f1.ID_FRIEND = f2.ID_FRIEND " +
                        " JOIN USERS u ON f1.ID_FRIEND = u.ID" +
                        " WHERE f1.ID_USER = ? AND f2.ID_USER = ?",
                userId, otherId);
        return commonFriends;
    }

    public boolean deleteFriend(long id, long friendId) {
        return delete("DELETE FROM friends WHERE ID_USER = ? AND ID_FRIEND =?", id, friendId);
    }

    public Optional<User> checkFriend(long userId, long friendId) {
        return findOne(
                "SELECT USERS.*" +
                        " FROM PUBLIC.FRIENDS" +
                        " LEFT JOIN PUBLIC.users ON FRIENDS.ID_FRIEND = users.ID" +
                        " WHERE FRIENDS.id_user = ? AND FRIENDS.id_friend = ?", userId, friendId);
    }

}

