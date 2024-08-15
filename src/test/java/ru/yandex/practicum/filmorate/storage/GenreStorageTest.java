package ru.yandex.practicum.filmorate.storage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.rowmappers.GenreRowMapper;

@JdbcTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class, GenreRowMapper.class})
@Transactional
public class GenreStorageTest {

  private final GenreDbStorage genreStorage;

  @Test
  @DisplayName("findAll() - returns all film Genres available.")
  public void findAllReturnsAllGenres() {
    final int expectedMpaCount = 6;

    final Collection<Genre> genres = genreStorage.findAll();

    assertThat(genres)
        .isNotNull()
        .isNotEmpty()
        .hasSize(expectedMpaCount)
        .allSatisfy(genre -> {
          assertThat(genre.getName()).isNotEmpty();
          assertThat(genre.getId()).isNotNull();
        });
    assertThat(genres).element(0)
        .hasFieldOrPropertyWithValue("id", 1L)
        .hasFieldOrPropertyWithValue("name", "Комедия");
    assertThat(genres).element(1)
        .hasFieldOrPropertyWithValue("id", 2L)
        .hasFieldOrPropertyWithValue("name", "Драма");
    assertThat(genres).element(2)
        .hasFieldOrPropertyWithValue("id", 3L)
        .hasFieldOrPropertyWithValue("name", "Мультфильм");
    assertThat(genres).element(3)
        .hasFieldOrPropertyWithValue("id", 4L)
        .hasFieldOrPropertyWithValue("name", "Триллер");
    assertThat(genres).element(4)
        .hasFieldOrPropertyWithValue("id", 5L)
        .hasFieldOrPropertyWithValue("name", "Документальный");
    assertThat(genres).element(5)
        .hasFieldOrPropertyWithValue("id", 6L)
        .hasFieldOrPropertyWithValue("name", "Боевик");
  }

  @Test
  @DisplayName("findById() - returns data of certain genre by valid ID.")
  public void findByIdReturnsGenreDataByValidId() {
    final Long id = 5L;

    final Optional<Genre> genreReturned = genreStorage.findById(id);

    assertThat(genreReturned)
        .isPresent()
        .hasValueSatisfying(genre ->
            assertThat(genre).hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("name", "Документальный"));

  }

  @Test
  @DisplayName("getGenresForFilm(Long) - returns list of genres for certain existed film.")
  public void getGenresForFilmReturnsListOFGenresAssociatedWithExistedFilm() {
    final Long filmId = 1L;
    final int expectedSize = 2;

    final Collection<Genre> genres = genreStorage.getGenresForFilm(filmId);

    assertThat(genres)
        .isNotNull()
        .isNotEmpty()
        .hasSize(2)
        .allSatisfy(genre -> {
          assertThat(genre.getName()).isNotEmpty();
          assertThat(genre.getId()).isNotNull();
        });
    assertThat(genres)
        .extracting("name")
        .containsExactlyInAnyOrder("Комедия", "Триллер");

  }

}
