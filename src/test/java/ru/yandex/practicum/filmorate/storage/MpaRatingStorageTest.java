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
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.dao.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.storage.rowmappers.MpaRatingRowMapper;

@JdbcTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaRatingDbStorage.class, MpaRatingRowMapper.class})
@Transactional
public class MpaRatingStorageTest {

  private final MpaRatingDbStorage mpaStorage;

  @Test
  @DisplayName("findAll() - returns all MPA rates available.")
  public void findAllReturnsAllMpaRates() {
    final int expectedMpaCount = 5;

    final Collection<MpaRating> mpas = mpaStorage.findAll();

    assertThat(mpas)
        .isNotNull()
        .isNotEmpty()
        .hasSize(expectedMpaCount)
        .allSatisfy(mpa -> {
          assertThat(mpa.getId()).isNotNull();
          assertThat(mpa.getName()).isNotEmpty();
        });
    assertThat(mpas).element(0)
        .hasFieldOrPropertyWithValue("id", 1L)
        .hasFieldOrPropertyWithValue("name", "G");
    assertThat(mpas).element(1)
        .hasFieldOrPropertyWithValue("id", 2L)
        .hasFieldOrPropertyWithValue("name", "PG");
    assertThat(mpas).element(2)
        .hasFieldOrPropertyWithValue("id", 3L)
        .hasFieldOrPropertyWithValue("name", "PG-13");
    assertThat(mpas).element(3)
        .hasFieldOrPropertyWithValue("id", 4L)
        .hasFieldOrPropertyWithValue("name", "R");
    assertThat(mpas).element(4)
        .hasFieldOrPropertyWithValue("id", 5L)
        .hasFieldOrPropertyWithValue("name", "NC-17");
  }

  @Test
  @DisplayName("findById() - returns data of certain MPA rate by valid ID.")
  public void findByIdReturnsMpaRateDataByValidId() {
    final Long id = 5L;

    final Optional<MpaRating> mpaReturned = mpaStorage.findById(id);

    assertThat(mpaReturned)
        .isPresent()
        .hasValueSatisfying(mpa ->
            assertThat(mpa).hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("name", "NC-17"));

  }

}
