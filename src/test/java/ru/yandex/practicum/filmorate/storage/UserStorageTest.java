package ru.yandex.practicum.filmorate.storage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.builder.TestDataBuilder;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.rowmappers.UserRowMapper;

@JdbcTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
@Transactional
public class UserStorageTest {

  private static final Long USER_ID_START = 5L;
  private final UserDbStorage userStorage;
  private final JdbcTemplate jdbc;

  @BeforeEach
  public void resetSequences() {
    jdbc.execute("ALTER TABLE \"user\" ALTER COLUMN id RESTART WITH " + USER_ID_START);
  }

  @Test
  @DisplayName("save(User) - saves user data to the db and returns saved user with generated ID.")
  public void saveReturnsUserDataWithId() {
    final User userToSave = TestDataBuilder.buildUser();

    final User returnedData = userStorage.save(userToSave);

    final Optional<User> userInDb = userStorage.findById(returnedData.getId());
    assertThat(userInDb)
        .isPresent()
        .hasValueSatisfying(user ->
            assertThat(user)
                .hasFieldOrPropertyWithValue("email", userToSave.getEmail())
                .hasFieldOrPropertyWithValue("login", userToSave.getLogin())
                .hasFieldOrPropertyWithValue("name", userToSave.getName())
                .hasFieldOrPropertyWithValue("birthday", userToSave.getBirthday()));
  }

  @Test
  @DisplayName("update(User) - changes existed in db user data and returns updated data.")
  public void updateChangesTheUserRecord() {
    final User dataToUpdate = TestDataBuilder.buildUser();
    final Long idToUpdate = 1L;
    dataToUpdate.setId(idToUpdate);

    userStorage.update(dataToUpdate);
    final Optional<User> changedUser = userStorage.findById(idToUpdate);

    assertThat(changedUser)
        .isPresent()
        .hasValueSatisfying(user ->
            assertThat(user)
                .hasFieldOrPropertyWithValue("email", dataToUpdate.getEmail())
                .hasFieldOrPropertyWithValue("login", dataToUpdate.getLogin())
                .hasFieldOrPropertyWithValue("name", dataToUpdate.getName())
                .hasFieldOrPropertyWithValue("birthday", dataToUpdate.getBirthday()));
  }

  @Test
  @DisplayName("findAll() - returns a collection of users from db.")
  public void findAllReturnsCollectionOfUsers() {
    final int expectedUserCount = 4;

    final Collection<User> users = userStorage.findAll();

    assertThat(users)
        .isNotNull()
        .isNotEmpty()
        .hasSize(expectedUserCount)
        .allSatisfy(user -> {
          assertThat(user.getEmail()).isNotEmpty();
          assertThat(user.getLogin()).isNotEmpty();
          assertThat(user.getName()).isNotEmpty();
          assertThat(user.getBirthday()).isNotNull();
        })
        .extracting("email")
        .containsExactlyInAnyOrder(
            "user1@gmail.com",
            "user2@yandex.ru",
            "user3@aol.com",
            "user4@yahoo.com"
        );
  }

  @Test
  @DisplayName("findById(Long) - returns a User data with correct ID.")
  public void findByIdReturnsUserDataWhenIdIsValid() {
    final Long id = 1L;

    final Optional<User> userData = userStorage.findById(id);

    assertThat(userData)
        .isPresent()
        .hasValueSatisfying(user -> {
          assertThat(user).hasFieldOrPropertyWithValue("id", id);
          assertThat(user.getEmail()).isNotEmpty();
          assertThat(user.getLogin()).isNotEmpty();
          assertThat(user.getName()).isNotEmpty();
          assertThat(user.getBirthday()).isNotNull();
          assertThat(user.getFriends()).isNotNull();
        });
  }

  @Test
  @DisplayName("addFriend(Long, Long) - saves a valid friend ID to existed User's friends list.")
  public void addFriendToTheExistedUserFriendListWhenIdIsValid() {
    final Long userId = 1L;
    final Long friend_id = 2L;

    userStorage.addFriend(userId, friend_id);
    final Optional<User> userData = userStorage.findById(userId);

    assertThat(userData)
        .isPresent()
        .hasValueSatisfying(user ->
            assertThat(user.getFriends())
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(friend_id));
  }

  @Test
  @DisplayName("getFriends(Long) - returns a not empty friend list for given valid User ID.")
  public void getFriendsReturnsListOfFriendsForGivenValidUserId() {
    final Long userId = 1L;
    final Long friendOne = 2L;
    final Long friendTwo = 3L;
    final int expectedSize = prepareFriendsList(userId, friendOne, friendTwo);

    final List<User> friends = userStorage.getFriends(userId);

    assertThat(friends)
        .isNotNull()
        .isNotEmpty()
        .hasSize(expectedSize)
        .anySatisfy(friend ->
            assertThat(friend)
                .hasFieldOrPropertyWithValue("id", friendOne))
        .anySatisfy(friend ->
            assertThat(friend)
                .hasFieldOrPropertyWithValue("id", friendTwo));
  }

  @Test
  @DisplayName("removeFriends(Long) - removes a friend from the user's friend list.")
  public void removeFriendFromTheUserFriendList() {
    final Long userId = 1L;
    final Long friendToRemove = 2L;
    final Long remainFriend = 3L;
    final int expectedSize = prepareFriendsList(userId, friendToRemove, remainFriend) - 1;

    userStorage.removeFriend(userId, friendToRemove);
    final List<User> friends = userStorage.getFriends(userId);

    assertThat(friends)
        .isNotNull()
        .isNotEmpty()
        .hasSize(expectedSize)
        .allSatisfy(friend ->
            assertThat(friend).hasFieldOrPropertyWithValue("id", remainFriend));
  }

  private int prepareFriendsList(final Long userHost, final Long... friendIds) {
    for (Long friendId : friendIds) {
      userStorage.addFriend(userHost, friendId);
    }
    return friendIds.length;
  }
}
