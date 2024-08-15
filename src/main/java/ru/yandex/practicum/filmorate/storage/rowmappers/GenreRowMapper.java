package ru.yandex.practicum.filmorate.storage.rowmappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

/**
 * A RowMapper implementation for mapping rows of a ResultSet to {@link Genre} instances.
 */
@Component
public class GenreRowMapper implements RowMapper<Genre> {

  @Override
  public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
    return Genre.builder()
        .id(rs.getLong("id"))
        .name(rs.getString("name"))
        .build();
  }
}
