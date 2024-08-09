package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class})
class UserDbStorageTest {
    private final UserDbStorage userDbStorage;
    private static User testUser;
    private static User newUser;

    @BeforeEach
    public void setUp(@Autowired UserDbStorage userStorage) {
        // Create a new test user
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setLogin("testUser");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.now().minusYears(20));
        newUser = userDbStorage.createUser(testUser);
    }

    @Test
    public void testCreateAndGetUser() {
        User user = userDbStorage.getUser(newUser.getId());

        assertThat(user)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", newUser.getId())
                .hasFieldOrPropertyWithValue("email", testUser.getEmail())
                .hasFieldOrPropertyWithValue("login", testUser.getLogin())
                .hasFieldOrPropertyWithValue("name", testUser.getName())
                .hasFieldOrPropertyWithValue("birthday", testUser.getBirthday());
    }

    @Test
    void getAll() {
        Collection<User> allUsers = userDbStorage.getAll();
        assertThat(allUsers)
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void update() {
        testUser.setName("Updated Test User");
        User updateUser = userDbStorage.update(testUser);
        assertThat(updateUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", newUser.getId())
                .hasFieldOrPropertyWithValue("name", "Updated Test User");
    }

    @Test
    void addGetFriend() {
        User friend = new User();
        friend.setEmail("test@example.com");
        friend.setLogin("testUser");
        friend.setName("Test User");
        friend.setBirthday(LocalDate.now().minusYears(20));
        friend = userDbStorage.createUser(friend);
        userDbStorage.addFriend(newUser.getId(), friend.getId());
        Collection<User> friends = userDbStorage.getFriends(newUser.getId());
        assertThat(friends)
                .isNotNull()
                .hasSize(1)
                .contains(friend);
    }

    @Test
    void deleteFriend() {
        userDbStorage.deleteFriend(newUser.getId(), testUser.getId());
        Collection<User> friends = userDbStorage.getFriends(newUser.getId());
        assertThat(friends)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void getCommonFriends() {
        User otherUser = new User();
        otherUser.setEmail("test@example.com");
        otherUser.setLogin("otherUser");
        otherUser.setName("Other User");
        otherUser.setBirthday(LocalDate.now().minusYears(20));
        otherUser = userDbStorage.createUser(otherUser);
        userDbStorage.addFriend(newUser.getId(), otherUser.getId());

        User otherUser2 = new User();
        otherUser2.setEmail("test@example.com");
        otherUser2.setLogin("otherUser");
        otherUser2.setName("Other User");
        otherUser2.setBirthday(LocalDate.now().minusYears(20));
        otherUser2 = userDbStorage.createUser(otherUser2);
        userDbStorage.addFriend(newUser.getId(), otherUser2.getId());
        userDbStorage.addFriend(otherUser.getId(), otherUser2.getId());

        Collection<User> commonFriends = userDbStorage.getCommonFriends(newUser.getId(), otherUser.getId());
        assertThat(commonFriends)
                .isNotNull()
                .hasSize(1)
                .contains(otherUser);
    }
}