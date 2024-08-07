package ru.yandex.practicum.filmorate.storage.rowmappers;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

/**
 * A RowMapper implementation for mapping rows of a ResultSet to {@link User} instances.
 * <p> This component is used to convert a row from a SQL query result into a {@link User} object.
 * It maps the columns of the ResultSet to the corresponding fields of the User class.
 */
@Component
public class UserRowMapper implements RowMapper<User> {

  @Override
  public User mapRow(ResultSet rs, int rowNum) throws SQLException {
    final User user = User.builder()
        .id(rs.getLong("id"))
        .email(rs.getString("email"))
        .login(rs.getString("login"))
        .name(rs.getString("name"))
        .birthday(Date.valueOf(rs.getString("birthday")).toLocalDate())
        .build();
    return user;
  }
}
