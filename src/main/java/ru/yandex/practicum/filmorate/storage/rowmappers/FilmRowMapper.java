package ru.yandex.practicum.filmorate.storage.rowmappers;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
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
        .build();

    final MpaRating mpa = MpaRating.builder()
        .id(rs.getLong("mpa_rating_id"))
        .name(rs.getString("mpa_name"))
        .build();

    film.setMpa(mpa);

    return film;
  }
}
