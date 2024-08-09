package ru.yandex.practicum.filmorate.builder;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

/**
 * Utility class for preparing test data
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestDataBuilder {

  public static User buildUser() {
    return User.builder()
        .email("qwerty@gmail.com")
        .login("testUser")
        .name("Bug Testerson")
        .birthday(LocalDate.of(1947, 9, 9))
        .build();
  }

  public static Film buildFilm() {
    return Film.builder()
        .name("New Film")
        .description("Some description")
        .releaseDate(LocalDate.of(2024, 8, 8))
        .duration(100L)
        .mpa(MpaRating.builder().id(1L).name("G").build())
        .build();
  }

  public static Film buildFilmWithGenre() {
    final Film film = Film.builder()
        .name("Film With One Genre")
        .description("Some description")
        .releaseDate(LocalDate.of(2023, 8, 8))
        .duration(100L)
        .mpa(MpaRating.builder().id(1L).name("G").build())
        .build();
    film.getGenres().add(Genre.builder().id(1).name("Комедия").build());
    return film;
  }

}
