package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Nested
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class})
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    Film testFilm;

    @BeforeEach
    public void setUp(@Autowired FilmDbStorage filmDbStorage) {
        testFilm = new Film();
        testFilm.setId(1L);
        testFilm.setName("Inception");
        testFilm.setDescription("A thief who steals corporate secrets through use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.");
        testFilm.setReleaseDate(LocalDate.of(2010, 7, 16));
        testFilm.setDuration(Duration.ofMinutes(148));
        testFilm.setMpa(1);
        testFilm.setGenres(new HashSet<>());
        testFilm.getGenres().add(1);
        testFilm.getGenres().add(2);
        testFilm = filmDbStorage.createFilm(testFilm);
    }

    @Test
    void createGetFilm() {
        Film newFilm = filmDbStorage.getFilm(testFilm.getId());
        assertThat(newFilm)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", testFilm.getId())
                .hasFieldOrPropertyWithValue("name", testFilm.getName())
                .hasFieldOrPropertyWithValue("description", testFilm.getDescription())
                .hasFieldOrPropertyWithValue("releaseDate", testFilm.getReleaseDate())
                .hasFieldOrPropertyWithValue("duration", testFilm.getDuration())
                .hasFieldOrPropertyWithValue("mpa", testFilm.getMpa())
                .hasFieldOrPropertyWithValue("genres", testFilm.getGenres());
    }

    @Test
    void update() {
        Film updateFilm = new Film();
        updateFilm.setId(testFilm.getId());
        updateFilm.setName("Update");
        updateFilm.setDescription("Update");
        updateFilm.setReleaseDate(LocalDate.of(2010, 7, 16));
        updateFilm.setDuration(Duration.ofMinutes(148));
        updateFilm.setMpa(1);
        updateFilm.setGenres(new HashSet<>());
        updateFilm.getGenres().add(1);

        filmDbStorage.update(updateFilm);
        assertThat(updateFilm)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", testFilm.getId())
                .hasFieldOrPropertyWithValue("name", "Update");
    }

    @Test
    void getAll() {
        Collection<Film> allFilms = filmDbStorage.getAll();
        assertThat(allFilms)
                .isNotNull()
                .hasSize(2);

    }

    @Test
    void addLike() {
        User testUser = setupUser();
        testFilm = filmDbStorage.addLike(testFilm.getId(), testUser.getId());
        Set<Long> like = testFilm.getUsersLike();
        assertThat(like)
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void deleteLike() {
        User testUser = setupUser();
        testFilm = filmDbStorage.addLike(testFilm.getId(), testUser.getId());

        testFilm = filmDbStorage.deleteLike(testFilm.getId(), testUser.getId());
        Set<Long> like = testFilm.getUsersLike();
        assertThat(like)
                .isNotNull()
                .hasSize(0);

    }

    @Test
    void getRating() {
        User testUser = new User();
        for (int i = 0; i < 2; i++) {
            testUser = setupUser();
            testFilm = filmDbStorage.addLike(testFilm.getId(), testUser.getId());
        }
        Collection<Film> films = filmDbStorage.getRating(1);
        assertThat(films)
                .isNotNull()
                .hasSize(1);
    }

    protected User setupUser() {
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setLogin("testUser");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.now().minusYears(20));
        testUser = userDbStorage.createUser(testUser);
        return testUser;
    }
}