package ru.yandex.practicum.filmorate.storage.rowmappers;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

/**
 * A RowMapper implementation for mapping rows from {@link ResultSet} to {@link Film} instance.
 */

@Component
public class FilmRowMapper implements RowMapper<Film> {

  @Override
  public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
    final Film film = Film.builder()
        .id(rs.getLong("id"))
        .name(rs.getString("name"))
        .description(rs.getString("description"))
        .releaseDate(Date.valueOf(rs.getString("release_date")).toLocalDate())
        .duration(rs.getLong("duration"))
        .mpa(MpaRating.builder()
            .id(rs.getLong("mpa_rating_id"))
            .name(rs.getString("mpa_name"))
            .build())
        .build();

    mapGenres(rs, film);
    mapLikes(rs, film);
    mapDirectors(rs, film);

    return film;
  }

  private void mapLikes(ResultSet rs, Film film) throws SQLException {
    final List<Long> likeIds = RowMapperHelper.extractListLong(rs, "like_id");
    likeIds.forEach(i -> film.getLikes().add(i));
  }


  private void mapGenres(ResultSet rs, Film film) throws SQLException {
    final List<Long> genreIds = RowMapperHelper.extractListLong(rs, "genre_id");
    final List<String> genreNames = RowMapperHelper.extractListString(rs, "genre_name");

    if (genreIds.size() != genreNames.size()) {
      throw new RuntimeException(
          "Error occur during FilmRowMapping - the number of genre ids and names differ.");
    }
    for (int i = 0; i < genreIds.size(); i++) {
      film.getGenres().add(Genre.builder().id(genreIds.get(i)).name(genreNames.get(i)).build());
    }
  }

  private void mapDirectors(ResultSet rs, Film film) throws SQLException {
    final List<Long> directorIds = RowMapperHelper.extractListLong(rs, "director_id");
    final List<String> directorNames = RowMapperHelper.extractListString(rs, "director_name");

    if (directorIds.size() != directorNames.size()) {
      throw new RuntimeException(
              "Error occur during FilmRowMapping - the number of director ids and names differ.");
    }
    for (int i = 0; i < directorIds.size(); i++) {
      film.getDirectors().add(
              Director.builder()
              .id(directorIds.get(i))
              .name(directorNames.get(i))
              .build());
    }
  }
}
