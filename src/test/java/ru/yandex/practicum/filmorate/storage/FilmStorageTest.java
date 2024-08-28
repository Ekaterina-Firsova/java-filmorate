package ru.yandex.practicum.filmorate.storage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.builder.TestDataBuilder;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SearchCriteria;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.rowmappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.rowmappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.rowmappers.UserRowMapper;

@JdbcTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class, UserDbStorage.class, UserRowMapper.class,
    GenreDbStorage.class, GenreRowMapper.class})
@Transactional
public class FilmStorageTest {

  private static final Long FILM_ID_START = 5L;
  private final FilmDbStorage filmStorage;
  private final JdbcTemplate jdbc;

  @BeforeEach
  public void resetSequences() {
    jdbc.execute("ALTER TABLE film ALTER COLUMN id RESTART WITH " + FILM_ID_START);
  }

  @Test
  @DisplayName("save(Film) - saves film data to the db and returns saved film with generated ID.")
  public void saveReturnsUserDataWithId() {
    final Film filmToSave = TestDataBuilder.buildFilm();

    final Film returnedData = filmStorage.save(filmToSave);

    final Optional<Film> filmDB = filmStorage.findById(returnedData.getId());
    assertThat(filmDB)
        .isPresent()
        .hasValueSatisfying(film -> {
          assertThat(film)
              .hasFieldOrPropertyWithValue("name", filmToSave.getName())
              .hasFieldOrPropertyWithValue("description", filmToSave.getDescription())
              .hasFieldOrPropertyWithValue("releaseDate", filmToSave.getReleaseDate())
              .hasFieldOrPropertyWithValue("duration", filmToSave.getDuration())
              .hasFieldOrPropertyWithValue("mpa", filmToSave.getMpa());
          assertThat(film.getGenres()).hasSize(0);
          assertThat(film.getLikes()).hasSize(0);
        });
  }

  @Test
  @DisplayName("update(Film) - changes existed in db film data and returns updated data.")
  public void updateChangesTheFilmRecord() {
    final Film dataToUpdate = TestDataBuilder.buildFilmWithGenre();
    final Long idToUpdate = 1L;
    dataToUpdate.setId(idToUpdate);

    filmStorage.update(dataToUpdate);
    final Optional<Film> changedFilm = filmStorage.findById(idToUpdate);

    assertThat(changedFilm)
        .isPresent()
        .hasValueSatisfying(film -> {
          assertThat(film)
              .hasFieldOrPropertyWithValue("name", dataToUpdate.getName())
              .hasFieldOrPropertyWithValue("description", dataToUpdate.getDescription())
              .hasFieldOrPropertyWithValue("releaseDate", dataToUpdate.getReleaseDate())
              .hasFieldOrPropertyWithValue("duration", dataToUpdate.getDuration())
              .hasFieldOrPropertyWithValue("mpa", dataToUpdate.getMpa());
          assertThat(film.getGenres())
              .hasSize(1)
              .allSatisfy(genre ->
                  assertThat(genre)
                      .hasFieldOrPropertyWithValue("id", 1L));
        });
  }

  @Test
  @DisplayName("findAll() - returns a collection of films from db.")
  public void findAllReturnsCollectionOfUsers() {
    final int expectedFilmCount = 4;

    final Collection<Film> films = filmStorage.findAll();

    assertThat(films)
        .isNotNull()
        .isNotEmpty()
        .hasSize(expectedFilmCount)
        .allSatisfy(film -> {
          assertThat(film.getName()).isNotEmpty();
          assertThat(film.getDescription()).isNotEmpty();
          assertThat(film.getReleaseDate()).isNotNull();
          assertThat(film.getDuration()).isNotNull();
          assertThat(film.getMpa()).isNotNull();
        })
        .extracting("id")
        .containsExactlyInAnyOrder(1L, 2L, 3L, 4L);
  }

  @Test
  @DisplayName("findById(Long) - returns a Film data with correct ID.")
  public void findByIdReturnsFilmDataWhenIdIsValid() {
    final Long id = 1L;

    final Optional<Film> filmData = filmStorage.findById(id);

    assertThat(filmData)
        .isPresent()
        .hasValueSatisfying(film -> {
          assertThat(film).hasFieldOrPropertyWithValue("id", id);
          assertThat(film.getName()).isNotEmpty();
          assertThat(film.getDescription()).isNotEmpty();
          assertThat(film.getReleaseDate()).isNotNull();
          assertThat(film.getDuration()).isNotNull();
          assertThat(film.getMpa()).isNotNull();
          assertThat(film.getGenres()).hasSize(2);
          assertThat(film.getLikes()).hasSize(0);
        });
  }

  @Test
  @DisplayName("addLike(Long,Long) - adds like to Film data with correct ID.")
  public void addLikeToTheExistedFilm() {
    final Long filmId = 1L;
    final Long userId = 1L;

    filmStorage.addLike(filmId, userId);
    final Film film = filmStorage.findById(filmId).orElseThrow();

    assertThat(film)
        .isNotNull()
        .hasFieldOrProperty("likes");
    assertThat(film.getLikes())
        .isNotEmpty()
        .hasSize(1)
        .contains(userId);
  }

  @Test
  @DisplayName("removeLike(Long,Long) - removes like from Film data with correct ID.")
  public void removeLikeFromTheExistedFilm() {
    final Long filmId = 1L;
    final Long userId = 1L;
    filmStorage.addLike(filmId, userId);

    filmStorage.removeLike(filmId, userId);
    final Film film = filmStorage.findById(filmId).orElseThrow();

    assertThat(film)
        .isNotNull()
        .hasFieldOrProperty("likes");
    assertThat(film.getLikes())
        .isEmpty();
  }

  /**
   * <li>Film(id=1) - has 0 like </li>
   * <li>Film(id=2) - has 3 likes [1,3,4] </li>
   * <li>Film(id=3) - has 1 likes [2] </li>
   * <li>Film(id=4) - has 4 likes [1,2,3,4] </li>
   */
  @Test
  @DisplayName("getTopFilms(Long, null, null) - returns top N amount the most liked by users films.")
  public void getTopFilmsWithTheMostLikesCountWhenGenreAndYearIsNull() {
    final int count = 4;

    final List<Film> topFilms = filmStorage.getTopFilms(count, null, null);

    assertThat(topFilms)
        .isNotNull()
        .isNotEmpty()
        .hasSize(count)
        .extracting("id")
        .containsExactly(4L, 2L, 3L, 1L);

  }

  @Test
  @DisplayName("getTopFilms(Long, Long, null) - returns top N amount the most liked by users films, filter in genre.")
  public void getTopFilmsWithTheMostLikesCountWhenYearIsNull() {
    final int count = 4;

    final List<Film> topFilms = filmStorage.getTopFilms(count, 2L, null);

    assertThat(topFilms)
        .isNotNull()
        .isNotEmpty()
        .hasSize(1)
        .extracting("id")
        .containsExactly(2L);
  }

  @Test
  @DisplayName("getTopFilms(Long, null, Integer) - returns top N amount the most liked by users films, filter in year.")
  public void getTopFilmsWithTheMostLikesCountWhenGenreIsNull() {
    final int count = 4;

    final List<Film> topFilms = filmStorage.getTopFilms(count, null, 2024);

    assertThat(topFilms)
        .isNotNull()
        .isNotEmpty()
        .hasSize(2)
        .extracting("id")
        .containsExactly(4L, 3L);
  }

  @Test
  @DisplayName(
      "getTopFilms(Long, Long, Integer) - returns top N amount the most liked by users films," +
          " filter in year and genre.")
  public void getTopFilmsWithTheMostLikesCount() {
    final int count = 4;

    final List<Film> topFilms = filmStorage.getTopFilms(count, 6L, 2024);

    assertThat(topFilms)
        .isNotNull()
        .isNotEmpty()
        .hasSize(1)
        .extracting("id")
        .containsExactly(4L);
  }

  @Test
  @DisplayName("getCommonFilms(Long, Long) - returns the common films between users.")
  public void getCommonFilms() {
    final int count = 1;

    final Collection<Film> films = filmStorage.getCommonFilms(1L, 2L);

    assertThat(films)
        .isNotNull()
        .isNotEmpty()
        .hasSize(count)
        .extracting("id")
        .containsExactly(4L);
  }

  @ParameterizedTest(name = "{0}, {1}")
  @DisplayName("Testing searching with valid parameters")
  @MethodSource("provideValidParameters")
  public void searchByValidParameters(final String query,
      final List<SearchCriteria> searchCriteria,
      final int size, final List<Long> filmId, final List<List<String>> director) {

    final List<Film> actual = filmStorage.searchBy(query, searchCriteria);

    assertThat(actual)
        .isNotNull()
        .isNotEmpty()
        .hasSize(size);
    for (int i = 0; i < size; i++) {
      final Film film = actual.get(i);
      assertThat(film.getId()).isEqualTo(filmId.get(i));
      assertThat(film.getDirectors())
          .extracting("name")
          .containsExactlyElementsOf(director.get(i));
    }
  }

  @ParameterizedTest(name = "{0}, {1}")
  @DisplayName("Testing searching with valid parameters and returning empty list")
  @MethodSource("provideValidParametersEmptyList")
  public void searchByValidParametersReturnsEmptyList(final String query, final List<SearchCriteria> searchCriteria) {

    final List<Film> actual = filmStorage.searchBy(query, searchCriteria);

    assertThat(actual)
        .isNotNull()
        .isEmpty();
  }

  private static Stream<Arguments> provideValidParameters() {
    return Stream.of(
        Arguments.of("travel", //query
            List.of(SearchCriteria.TITLE), //searchCriteria
            1, //result List size
            List.of(1L), //IDs of the films found
            List.of(List.of("Robert Zemeckis"))), //Director names for the films found

        Arguments.of("travel",
            List.of(SearchCriteria.TITLE, SearchCriteria.DIRECTOR),
            1,
            List.of(1L),
            List.of(List.of("Robert Zemeckis"))),

        Arguments.of("the",
            List.of(SearchCriteria.TITLE, SearchCriteria.DIRECTOR),
            3,
            List.of(4L, 3L, 1L),
            List.of(
                List.of(),
                List.of("Alfred Hitchcock"),
                List.of("Robert Zemeckis")))
    );
  }

  private static Stream<Arguments> provideValidParametersEmptyList() {
    return Stream.of(
        Arguments.of("travel", List.of(SearchCriteria.DIRECTOR)),
        Arguments.of("CANT FIND ANYTHING", List.of(SearchCriteria.TITLE, SearchCriteria.DIRECTOR))
    );
  }

}